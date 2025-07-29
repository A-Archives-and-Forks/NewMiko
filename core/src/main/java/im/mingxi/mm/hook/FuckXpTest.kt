package im.mingxi.mm.hook

import com.highcapable.kavaref.KavaRef.Companion.resolve
import im.mingxi.miko.hook.SwitchHook
import im.mingxi.miko.ui.util.FuncRouter
import im.mingxi.miko.util.dexkit.DexMethodDescriptor

// @FunctionHookEntry(itemType = FunctionHookEntry.WECHAT_ITEM)
class FuckXpTest : SwitchHook() { //, IFinder {
    override val name: String
        get() = "防止微信检测"
    override val description: CharSequence
        get() = "google play版微信请勿打开否则模块失效"
    override val uiItemLocation: String
        get() = FuncRouter.EXPERIMENTAL

    override fun initOnce(): Boolean {
        ClassLoader::class.resolve().firstMethod {
            name = "loadClass"
            parameters(String::class.java)
        }.self.hookBeforeIfEnable { param ->
            if (param.args[0] != null && param.args[0].toString()
                    .startsWith("de.robv.android.xposed")
            ) {
                param.args[0] = "com.tencent.mm.abcd"
            }
        }

        TargetMethod.toMethod(loader).hookBeforeIfEnable { param ->
            param.resultFalse()
        }

        return true
    }

    private val TargetMethod = DexMethodDescriptor(this, "$simpleTAG.Method.TargetMethod")

//    override fun dexFind(finder: DexFinder) {
//        with(finder) {
//            TargetMethod.findDexMethod {
//                searchPackages("com.tencent.mm.app")
//                matcher {
//                    usingStrings("de.robv.android.xposed.XposedBridge")
//                }
//            }
//        }
//    }
}