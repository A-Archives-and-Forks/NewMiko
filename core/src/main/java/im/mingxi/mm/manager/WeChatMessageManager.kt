package im.mingxi.mm.manager

interface WeChatMessageManager {
    fun sendText(wxId: String, content: String)
}