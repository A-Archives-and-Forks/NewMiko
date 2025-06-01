package im.mingxi.common

import im.mingxi.miko.annotation.FunctionHookEntry
import im.mingxi.miko.hook.BaseFuncHook

@FunctionHookEntry(itemName = "莫奈取色", itemType = FunctionHookEntry.COMMON_ITEM)
class MonetColorEnable : BaseFuncHook(defaultEnabled = true) {
    override fun initOnce(): Boolean {

        return true
    }
}