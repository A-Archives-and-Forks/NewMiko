package im.mingxi.mm.manager

interface WeChatMessageManager {
    fun sendText(talker: String, content: String)

    fun insertSysMsg(talker: String, content: String, msgId: Long)

    fun sendImage(talker: String, imagePath: String, appId: String? = null)
}