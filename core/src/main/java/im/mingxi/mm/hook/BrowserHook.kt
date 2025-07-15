package im.mingxi.mm.hook

import android.app.Activity
import android.content.Intent
import androidx.core.net.toUri
import im.mingxi.miko.annotation.FunctionHookEntry
import im.mingxi.miko.hook.SwitchHook
import im.mingxi.miko.ui.util.FuncRouter
import im.mingxi.miko.util.Reflex
import im.mingxi.miko.util.hookAfterIfEnable


@FunctionHookEntry
class BrowserHook : SwitchHook() {
    override val name: String
        get() = "浏览Link默认启用系统浏览器"
    override val uiItemLocation: String
        get() = FuncRouter.AMUSEMENT

    override fun initOnce(): Boolean {
        hookAfterIfEnable(
            Reflex.findMethod(
                loader.loadClass("com.tencent.mm.plugin.webview.ui.tools.WebViewUI")
            ).setMethodName("onCreate").get()
        ) {
            val app = it.thisObject as Activity
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setData(app.intent.getStringExtra("rawUrl")!!.toUri())
            app.startActivity(intent)
            app.finish()
        }
        return true
    }

}