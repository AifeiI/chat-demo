package com.aifeii.phonedemo.data.model.message

import com.aifeii.phonedemo.data.model.User

/**
 * Created by Jiaming.Luo on 2019/10/17.
 */
interface BaseMessage {

    val sender: User

    val action: MessageAction

    val content: String

}