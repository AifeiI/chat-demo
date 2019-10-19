package com.aifeii.phonedemo.component.client.channel

import com.aifeii.phonedemo.component.BaseMessageListener
import com.aifeii.phonedemo.data.model.message.BaseMessage

/**
 * Created by Jiaming.Luo on 2019/10/17.
 */
interface MessageChannel {

    fun connect()

    fun isConnected(): Boolean

    fun isClosed(): Boolean

    fun close()

    fun send(message: BaseMessage)

    fun setOnMessageListener(listener: BaseMessageListener)

}