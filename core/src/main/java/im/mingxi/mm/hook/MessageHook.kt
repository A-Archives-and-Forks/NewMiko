package im.mingxi.mm.hook

import im.mingxi.miko.hook.SwitchHook
import im.mingxi.miko.ui.util.FuncRouter

// @FunctionHookEntry(itemType = FunctionHookEntry.WECHAT_ITEM)
class MessageHook : SwitchHook(defaultEnabled = true) {
    override val name: String
        get() = "消息测试[开发者]"
    override val uiItemLocation: String
        get() = FuncRouter.CHAT

    override fun initOnce(): Boolean {
        //WeChatMessageManagerImpl().sendText("wxid_xh5txgo29pv522", "你好")
        return true
    }
}