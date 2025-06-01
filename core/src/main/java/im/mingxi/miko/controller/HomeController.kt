package im.mingxi.miko.controller

import android.content.Intent
import im.mingxi.loader.bridge.XPBridge
import im.mingxi.miko.ui.activity.HomeActivity
import im.mingxi.miko.util.HookEnv

object HomeController {
    fun openHomeActivity() {
        val activity = HookEnv.hostActivity
        val intent = Intent(activity, HomeActivity::class.java)
        XPBridge.log("openHomeActivity")
        activity.startActivity(intent)

    }
}