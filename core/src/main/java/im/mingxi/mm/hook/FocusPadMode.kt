package im.mingxi.mm.hook

import im.mingxi.miko.annotation.FunctionHookEntry
import im.mingxi.miko.hook.SwitchHook
import im.mingxi.miko.ui.util.FuncRouter
import im.mingxi.miko.util.dexkit.DexFinder
import im.mingxi.miko.util.dexkit.DexMethodDescriptor
import im.mingxi.miko.util.dexkit.IFinder

@FunctionHookEntry(itemName = "强制平板模式", itemType = FunctionHookEntry.WECHAT_ITEM)
class FocusPadMode : SwitchHook(), IFinder {
    private val isPadDevice =
        DexMethodDescriptor(mConfig = "${simpleTAG}.Method.isPadDevice", mBaseFuncHook = this)
    override val name: String
        get() = "强制平板模式"
    override val uiItemLocation: String
        get() = FuncRouter.EXPERIMENTAL

    override fun initOnce(): Boolean {
        isPadDevice.toMethod(loader).hookBeforeIfEnable {
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