package im.mingxi.mm.hook

import im.mingxi.miko.annotation.FunctionHookEntry
import im.mingxi.miko.hook.BaseFuncHook
import im.mingxi.miko.util.dexkit.DexFinder
import im.mingxi.miko.util.dexkit.DexMethodDescriptor
import im.mingxi.miko.util.dexkit.IFinder
import im.mingxi.miko.util.hookBeforeIfEnable
import org.luckypray.dexkit.query.FindMethod
import org.luckypray.dexkit.query.matchers.MethodMatcher

@FunctionHookEntry(itemName = "强制平板模式", itemType = FunctionHookEntry.WECHAT_ITEM)
class FocusPadMode : BaseFuncHook(defaultEnabled = true), IFinder {
    val isPadDevice =
        DexMethodDescriptor(mConfig = "${simpleTAG}.Method.isPadDevice", mBaseFuncHook = this)

    override fun initOnce(): Boolean {
        isPadDevice.toMethod(loader).hookBeforeIfEnable(this) {
            it.resultTrue()
        }
        return true
    }

    override fun dexFind(finder: DexFinder) {
        finder.findDexMethod(
            isPadDevice,
            FindMethod().matcher(MethodMatcher().usingStrings("Lenovo TB-9707F"))
        )
    }
}