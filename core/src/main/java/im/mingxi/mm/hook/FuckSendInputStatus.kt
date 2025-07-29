package im.mingxi.mm.hook

import com.highcapable.kavaref.KavaRef.Companion.resolve
import im.mingxi.miko.annotation.FunctionHookEntry
import im.mingxi.miko.hook.SwitchHook
import im.mingxi.miko.ui.util.FuncRouter
import im.mingxi.miko.util.dexkit.DexDesc
import im.mingxi.miko.util.dexkit.IFinder
import org.luckypray.dexkit.DexKitBridge

@FunctionHookEntry(itemType = FunctionHookEntry.WECHAT_ITEM)
class FuckSendInputStatus : SwitchHook(), IFinder {
    override val name: String
        get() = "去你妈的传输输入状态"
    override val uiItemLocation: String
        get() = FuncRouter.CHAT

    override fun initOnce(): Boolean {

        TypingSend.toConstructor().resolve().firstMethod {
            name = "doScene"
        }.self.hookBeforeIfEnable { param ->
            param.result = 0
        }
        return true
    }

    private object TypingSend : DexDesc("FuckSendInputStatus.TypingSend")

    override fun dexFind(finder: DexKitBridge) {

        TypingSend.findDexMethod(finder) {
            searchPackages("com.tencent.mm.modelsimple")
            matcher {
                usingStrings("null cannot be cast to non-null type com.tencent.mm.protocal.MMTypingSend.Req")
            }
        }

    }
}