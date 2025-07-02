package im.mingxi.miko.ui.util

import android.annotation.SuppressLint
import im.mingxi.miko.ui.activity.HomeActivity

@SuppressLint("StaticFieldLeak")
object ProxyActUtil {
    var mApp: HomeActivity? = null

    @JvmStatic
    fun isProxyActRunning(): Boolean {
        return if (mApp == null || mApp!!.isDestroyed) return false else true
    }

}