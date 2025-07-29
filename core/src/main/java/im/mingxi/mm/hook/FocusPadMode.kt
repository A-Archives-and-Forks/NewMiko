package im.mingxi.mm.hook

import im.mingxi.miko.annotation.FunctionHookEntry
import im.mingxi.miko.hook.SwitchHook
import im.mingxi.miko.ui.util.FuncRouter
import im.mingxi.miko.util.dexkit.DexDesc
import im.mingxi.miko.util.dexkit.IFinder
import org.luckypray.dexkit.DexKitBridge

@FunctionHookEntry(itemName = "强制平板模式", itemType = FunctionHookEntry.WECHAT_ITEM)
class FocusPadMode : SwitchHook(), IFinder {
    override val name: String
        get() = "强制平板模式"
    override val uiItemLocation: String
        get() = FuncRouter.EXPERIMENTAL


    private object MethodIsPadDevice : DexDesc("FocusPadMode.MethodIsPadDevice")

    override fun initOnce(): Boolean {
        MethodIsPadDevice.toMethod().hookBeforeIfEnable {
            it.resultTrue()
        }
        return true
    }

    override fun dexFind(dexKit: DexKitBridge) {

        MethodIsPadDevice.findDexMethod(dexKit) {
            searchPackages("com.tencent.mm.ui")
                matcher {
                    usingStrings("Lenovo TB-9707F")
                }
            }
        }

}