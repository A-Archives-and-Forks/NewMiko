package im.mingxi.mm.hook

import com.highcapable.kavaref.KavaRef.Companion.resolve
import im.mingxi.core.R
import im.mingxi.miko.annotation.FunctionHookEntry
import im.mingxi.miko.hook.SwitchHook
import im.mingxi.miko.ui.util.FuncRouter
import im.mingxi.miko.util.HookEnv
import im.mingxi.miko.util.toAppClass

@FunctionHookEntry(itemType = FunctionHookEntry.WECHAT_ITEM)
class BubbleHook : SwitchHook() {
    override val name: String
        get() = "启用气泡美化"
    override val uiItemLocation: String
        get() = FuncRouter.BEAUTIFY

    override fun initOnce(): Boolean {
        val target = "com.tencent.mm.ui.widget.MMNeat7extView".toAppClass()!!
            .resolve()
            .firstMethod {
                name = "setBackground"
                parameterCount(1)
            }.self
        target.hookBeforeIfEnable {
            val drawable = HookEnv.hostContext.getDrawable(R.drawable.bubble)
            it.args[0] = drawable
        }
        return true
    }
}