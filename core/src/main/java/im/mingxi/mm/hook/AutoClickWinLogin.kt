package im.mingxi.mm.hook

import android.app.Activity
import android.widget.Button
import im.mingxi.loader.bridge.XPBridge
import im.mingxi.miko.annotation.FunctionHookEntry
import im.mingxi.miko.hook.BaseFuncHook
import im.mingxi.miko.util.Reflex

/**
 * Author: HdShare
 */
@FunctionHookEntry(itemName = "自动点击电脑登录", itemType = FunctionHookEntry.WECHAT_ITEM)
class AutoClickWinLogin : BaseFuncHook(defaultEnabled = true) {
    override fun initOnce(): Boolean {
        val clazz = Reflex.loadClass("com.tencent.mm.plugin.webwx.ui.ExtDeviceWXLoginUI")
        val method = Reflex.findMethod(clazz).setMethodName("initView").get()
        XPBridge.hookAfter(method) {
            val field = Reflex.findField(clazz).setReturnType(Button::class.java).get()
            val activity = it.thisObject as Activity
            (field.get(activity) as Button).callOnClick()
        }
        return true
    }
}
