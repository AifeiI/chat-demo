package com.aifeii.phonedemo.component.server

import android.util.Log
import com.aifeii.phonedemo.component.BaseMessageListener
import com.aifeii.phonedemo.data.model.message.BaseMessage
import com.google.gson.Gson
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.disposables.Disposable
import java.io.IOException
import java.net.Socket

/**
 * Created by Jiaming.Luo on 2019/10/17.
 */
class MessageHandler(
    val socket: Socket
) {

    private val gson = Gson()
    private var disposable: Disposable? = null

    fun isClosed(): Boolean {
        return socket.isClosed
    }

    fun connect(listener: BaseMessageListener) {
        disposable = Flowable.create<BaseMessage>({ emitter ->
            while (true) {
                if (emitter.isCancelled) {
                    return@create
                }
                if (socket.isClosed) {
                    return@create
                }
                try {
                    val data = socket.getInputStream().readBytes()
                    val baseMessage =
                        gson.fromJson<BaseMessage>(String(data), BaseMessage::class.java)
                    emitter.onNext(baseMessage)
                } catch (e: IOException) {
                    emitter.onError(e)
                }
            }
        }, BackpressureStrategy.BUFFER)
            .doOnNext {
                listener.onReceive(it)
            }
            .subscribe()
    }

    fun close() {
        disposable?.dispose()
        if (!socket.isClosed) {
            try {
                socket.close()
            } catch (e: IOException) {
                e.printStackTrace()
                Log.w(
                    "MessageHandler",
                    "Close the connect(${socket.inetAddress}:${socket.port}) is failure"
                )
            }
        }
    }

}