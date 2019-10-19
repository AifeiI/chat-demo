package com.aifeii.phonedemo.component.radar

import com.aifeii.phonedemo.component.DefaultPort
import com.aifeii.phonedemo.data.model.radar.Biu
import com.aifeii.phonedemo.data.model.radar.Device
import com.google.gson.Gson
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.processors.PublishProcessor
import io.reactivex.schedulers.Schedulers
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by Jiaming.Luo on 2019/10/18.
 */
class DeviceRadar private constructor() {

    var post: Int = DefaultPort.DEFAULT_RADAR_LISTENER_PORT

    private val gson = Gson()
    private val receive: DatagramPacket = DatagramPacket(ByteArray(1024), 1024)

    private var server: DatagramSocket? = null
    private var disposable: Disposable? = null
    private var scanDisposable: Disposable? = null

    private val bus = PublishProcessor.create<Device>()

    fun start() {
        if (disposable != null && !disposable!!.isDisposed) {
            return
        }

        if (server != null && !server!!.isClosed) {
            return
        }

        disposable = Observable.create<Device> { emitter ->
            server = DatagramSocket(post)

            while (server != null) {
                if (emitter.isDisposed) {
                    return@create
                }
                if (server!!.isClosed) {
                    return@create
                }

                server!!.receive(receive)
                val socketAddress = receive.socketAddress.toString()
                val recvByte = Arrays.copyOfRange(receive.data, 0, receive.length)
                val dataString = String(recvByte)
                println("Receive: $dataString from $socketAddress")
                val biu = gson.fromJson<Biu>(dataString, Biu::class.java)
                if (biu.text == "Hi") {
                    val addressString = socketAddress.replace("/", "")
                    val addressSplit = addressString.split(':')
                    emitter.onNext(
                        Device(
                            ip = addressSplit[0],
                            port = addressSplit[1].toInt(),
                            mac = "",
                            name = ""
                        )
                    )
                }
            }
        }
            .subscribeOn(Schedulers.io())
            .doOnNext { bus.onNext(it) }
            .subscribe()
    }

    fun shutdown() {
        disposable?.dispose()
        scanDisposable?.dispose()
    }

    fun onListener(): Flowable<Device> {
        return bus
    }

    fun startScan() {
        if (scanDisposable != null && !scanDisposable!!.isDisposed) {
            return
        }

        val biu = Biu(text = "Hi")
        val buf = gson.toJson(biu).toByteArray()
        val inetAddress = InetAddress.getByName("255.255.255.255")
//        val inetAddress = InetAddress.getByName("10.38.178.47")     // Send To Nexus 6
//        val inetAddress = InetAddress.getByName("10.38.178.155")  // Send To Pixel 2 XL
        val client = DatagramSocket()

        scanDisposable = Observable.interval(3000, TimeUnit.MILLISECONDS)
            .doOnNext {
                val sendPack = DatagramPacket(buf, buf.size, inetAddress, post)
                client.send(sendPack)
            }
            .subscribe()
    }

    fun stopScan() {
        scanDisposable?.dispose()
    }

    companion object {
        val instance by lazy {
            DeviceRadar()
        }
    }
}