package im.mingxi.common

import android.widget.TextView
import im.mingxi.loader.util.Constants
import im.mingxi.miko.annotation.FunctionHookEntry
import im.mingxi.miko.hook.SwitchHook
import im.mingxi.miko.ui.util.FuncRouter
import im.mingxi.miko.util.Reflex


@FunctionHookEntry(itemType = Constants.COMMON_ITEM)
class FuckLargeText : SwitchHook() {
    override val name: String
        get() = "屏蔽卡屏文本"
    override val uiItemLocation: String
        get() = FuncRouter.SIMPLIFY

    val target = Reflex.findMethod(TextView::class.java).setMethodName("setText").get()

    override fun initOnce(): Boolean {
        target.hookBeforeIfEnable { param ->
            if (param.args[0] is CharSequence) {
                val msg = param.args[0] as CharSequence?
                if (msg == null) return@hookBeforeIfEnable
                if (msg.length > 3000) {
                    param.args[0] = "检测到卡屏文本，已进行屏蔽"
                }
            }
        }
        return true
    }
}