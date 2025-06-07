package im.mingxi.mm.hook

import android.app.Activity
import android.widget.Button
import im.mingxi.miko.annotation.FunctionHookEntry
import im.mingxi.miko.hook.SwitchHook
import im.mingxi.miko.util.Reflex
import im.mingxi.miko.util.hookAfterIfEnable

/**
 * Author: HdShare
 */
@FunctionHookEntry(itemName = "自动点击电脑登录", itemType = FunctionHookEntry.WECHAT_ITEM)
class AutoClickWinLogin : SwitchHook() {
    override val name: String
        get() = "自动点击电脑登录"
    override val uiItemLocation: Array<String>
        get() = arrayOf("娱乐", "实验性功能")

    override fun initOnce(): Boolean {
        val clazz = Reflex.loadClass("com.tencent.mm.plugin.webwx.ui.ExtDeviceWXLoginUI")
        val method = Reflex.findMethod(clazz).setMethodName("initView").get()
        hookAfterIfEnable(method) {
            val field = Reflex.findField(clazz).setReturnType(Button::class.java).get()
            val activity = it.thisObject as Activity
            (field.get(activity) as Button).callOnClick()
        }
        return true
    }
}
