package im.mingxi.mm.hook

import im.mingxi.miko.annotation.FunctionHookEntry
import im.mingxi.miko.hook.BaseFuncHook
import im.mingxi.miko.util.dexkit.DexFinder
import im.mingxi.miko.util.dexkit.DexMethodDescriptor
import im.mingxi.miko.util.dexkit.IFinder
import im.mingxi.miko.util.hookBeforeIfEnable

@FunctionHookEntry(itemName = "强制平板模式", itemType = FunctionHookEntry.WECHAT_ITEM)
class FocusPadMode : BaseFuncHook(defaultEnabled = true), IFinder {
    private val isPadDevice =
        DexMethodDescriptor(mConfig = "${simpleTAG}.Method.isPadDevice", mBaseFuncHook = this)

    override fun initOnce(): Boolean {
        isPadDevice.toMethod(loader).hookBeforeIfEnable(this) {
            it.resultTrue()
        }
        return true
    }

    override fun dexFind(finder: DexFinder) {
        with(finder) {
            isPadDevice.findDexMethod {
                matcher {
                    usingStrings("Lenovo TB-9707F")
                }
            }
        }
    }
}