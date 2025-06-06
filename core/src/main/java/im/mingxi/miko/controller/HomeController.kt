package im.mingxi.miko.controller

import android.content.Intent
import im.mingxi.miko.ui.activity.HomeActivity
import im.mingxi.miko.util.HookEnv

object HomeController {
    fun openHomeActivity() {
        val activity = HookEnv.hostActivity
        val intent = Intent(activity, HomeActivity::class.java)
        activity.startActivity(intent)
    }
}