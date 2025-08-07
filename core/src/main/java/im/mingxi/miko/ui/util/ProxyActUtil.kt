package im.mingxi.miko.ui.util

import android.annotation.SuppressLint
import im.mingxi.miko.ui.activity.HomeActivityV2

@SuppressLint("StaticFieldLeak")
object ProxyActUtil {
    var mApp: HomeActivityV2? = null

    @JvmStatic
    fun isProxyActRunning(): Boolean {
        return if (mApp == null || mApp!!.isDestroyed) return false else true
    }

}