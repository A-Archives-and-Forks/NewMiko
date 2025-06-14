package im.mingxi.miko.ui.util

import android.annotation.SuppressLint
import android.app.Activity

@SuppressLint("StaticFieldLeak")
object ProxyActUtil {
    var mApp: Activity? = null

    fun isProxyActRunning(): Boolean {
        return if (mApp == null || mApp!!.isDestroyed) return true else false
    }
}