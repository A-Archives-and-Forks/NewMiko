package im.mingxi.mm.hook

import android.app.Activity
import android.content.Intent
import im.mingxi.loader.bridge.XPBridge
import im.mingxi.loader.bridge.XPBridge.HookParam
import im.mingxi.miko.annotation.FunctionHookEntry
import im.mingxi.miko.hook.SwitchHook
import im.mingxi.miko.util.Reflex


@FunctionHookEntry
class BrowserHook : SwitchHook() {
    override val name: String
        get() = "浏览Link默认启用系统浏览器"
    override val uiItemLocation: Array<String>
        get() = arrayOf("娱乐", "其他")

    override fun initOnce(): Boolean {
        XPBridge.hookAfter(
            Reflex.findMethod(Activity::class.java).setMethodName("getIntent").get()
        ) { param: HookParam ->

            XPBridge.log((param.result as Intent).extras.toString())
        }
        return true
    }

}