package com.aifeii.phonedemo.component.client.channel

import com.aifeii.phonedemo.component.BaseMessageListener
import com.aifeii.phonedemo.data.model.message.BaseMessage
import com.google.gson.Gson
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.io.IOException
import java.net.Socket

/**
 * Created by Jiaming.Luo on 2019/10/17.
 */
class SocketChannel private constructor() : MessageChannel {

    private var host: String = ""
    private var post: Int = -1
    private var socketClient: Socket? = null

    private var listener: BaseMessageListener? = null
    private var disposable: Disposable? = null

    private val gson = Gson()
    private val bus = PublishSubject.create<BaseMessage>()

    constructor(host: String, post: Int) : this() {
        this.host = host
        this.post = post
    }

    override fun connect() {
        if (socketClient != null && !socketClient!!.isClosed) {
            return
        }

        socketClient = Socket(host, post)

        if (socketClient != null && isConnected()) {
            disposable = Flowable.create<BaseMessage>(
                { emitter ->
                    while (true) {
                        if (emitter.isCancelled) {
                            return@create
                        }
                        if (socketClient!!.isClosed) {
                            return@create
                        }
                        try {
                            val data = socketClient!!.getInputStream().readBytes()
                            val baseMessage = gson.fromJson<BaseMessage>(String(data), BaseMessage::class.java)
                            emitter.onNext(baseMessage)
                        } catch (e: IOException) {
                            emitter.onError(e)
                        }
                    }
                }, BackpressureStrategy.BUFFER
            )
                .subscribeOn(Schedulers.io())
                .doOnNext {
                    listener?.onReceive(it)
                }
                .subscribe()
        }
    }

    override fun isConnected(): Boolean {
        return socketClient?.isConnected ?: false
    }

    override fun isClosed(): Boolean {
        return socketClient?.isClosed ?: false
    }

    override fun close() {
        socketClient?.close()
    }

    override fun send(message: BaseMessage) {
        val data = gson.toJson(message)
        socketClient?.getOutputStream()?.write(data.toByteArray())
    }

    override fun setOnMessageListener(listener: BaseMessageListener) {
        this.listener = listener
    }

}