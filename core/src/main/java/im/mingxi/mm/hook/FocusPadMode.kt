package im.mingxi.mm.hook

import im.mingxi.miko.annotation.FunctionHookEntry
import im.mingxi.miko.hook.BaseFuncHook

@FunctionHookEntry(itemName = "强制平板模式", itemType = FunctionHookEntry.WECHAT_ITEM)
class FocusPadMode : BaseFuncHook(defaultEnabled = true) {
    override fun initOnce(): Boolean {
        return true
    }
}