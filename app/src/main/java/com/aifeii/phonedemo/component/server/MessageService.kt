package com.aifeii.phonedemo.component.server

import android.util.Log
import com.aifeii.phonedemo.component.BaseMessageListener
import com.aifeii.phonedemo.component.DefaultPort
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.IOException
import java.net.ServerSocket
import java.net.Socket

/**
 * Created by Jiaming.Luo on 2019/10/17.
 */
class MessageService(
    val post: Int = DefaultPort.DEFAULT_MESSAGE_SERVICE_PORT,
    var listener: BaseMessageListener
) {

    private var server: ServerSocket? = null
    private val messageHandlerList = ArrayList<MessageHandler>()

    private var disposable: Disposable? = null

    fun start() {
        if (server != null && !server!!.isClosed) {
            return
        }

        server = ServerSocket(post)

        if (server != null) {
            disposable = Flowable.create<Socket>(
                { emitter ->
                    while (true) {
                        if (emitter.isCancelled) {
                            return@create
                        }
                        if (server!!.isClosed) {
                            return@create
                        }
                        val socket = server!!.accept()
                        emitter.onNext(socket)
                    }
                },
                BackpressureStrategy.DROP
            )
                .subscribeOn(Schedulers.io())
                .map { MessageHandler(it) }
                .doOnNext { messageHandlerList.add(it) }
                .subscribe {
                    it.connect(listener)
                }

        }
    }

    fun shutdown() {
        disposable?.dispose()
        if (server?.isClosed == false) {
            for (handler in messageHandlerList) {
                if (!handler.socket.isClosed) {
                    try {
                        handler.socket.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Log.d("MessageService", "The client(${handler.socket}) close is failure")
                    }
                }
            }
            messageHandlerList.clear()
            server!!.close()
        }

    }

}