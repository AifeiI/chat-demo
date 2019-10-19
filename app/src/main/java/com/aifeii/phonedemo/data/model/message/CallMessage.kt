package com.aifeii.phonedemo.data.model.message

import com.aifeii.phonedemo.data.model.User

/**
 * Created by Jiaming.Luo on 2019/10/17.
 */
data class CallMessage(
    override val sender: User = User(),
    override val content: String = ""
) : BaseMessage {

    override val action: MessageAction = MessageAction.CALL

}