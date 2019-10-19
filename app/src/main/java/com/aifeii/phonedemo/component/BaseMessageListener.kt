package com.aifeii.phonedemo.component

import com.aifeii.phonedemo.data.model.message.BaseMessage

/**
 * Created by Jiaming.Luo on 2019/10/17.
 */
interface BaseMessageListener {

    fun onReceive(message: BaseMessage)

}