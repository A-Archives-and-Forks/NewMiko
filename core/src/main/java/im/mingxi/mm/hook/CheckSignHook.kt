package im.mingxi.mm.hook

import im.mingxi.miko.annotation.FunctionHookEntry
import im.mingxi.miko.hook.SwitchHook
import im.mingxi.miko.ui.util.FuncRouter
import im.mingxi.miko.util.dexkit.DexDesc
import im.mingxi.miko.util.dexkit.IFinder
import im.mingxi.miko.util.hookAfterIfEnable
import org.luckypray.dexkit.DexKitBridge

@FunctionHookEntry(itemType = FunctionHookEntry.WECHAT_ITEM)
class CheckSignHook : SwitchHook(), IFinder {
    override val name: String
        get() = "去除app签名校验"
    override val uiItemLocation: String
        get() = FuncRouter.EXPERIMENTAL

    private val CheckSign = DexDesc("$simpleTAG.Method.CheckSign")

    override fun initOnce(): Boolean {
        hookAfterIfEnable(CheckSign.toMethod()) { it.resultTrue() }
        return true
    }

    override fun dexFind(finder: DexKitBridge) =

        CheckSign.findDexMethod(finder) {
            searchPackages("com.tencent.mm.pluginsdk.model.app")

            matcher {
                usingStrings("checkAppSignature server signatures:%s", "MicroMsg.AppUtil")
            }

        }

}