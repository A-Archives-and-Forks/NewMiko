package im.mingxi.miko.hook

import android.util.Log
import im.mingxi.loader.bridge.XPBridge

abstract class BaseFuncHook(val defaultEnabled: Boolean = false) {
    val TAG: String = this.javaClass.name
    val mErrors: ArrayList<Throwable> = ArrayList()
    var isInitialize: Boolean = false

    @Throws(Throwable::class)
    abstract fun initOnce(): Boolean

    fun isEnabled(): Boolean {
        return defaultEnabled
    }

    fun initialize() {
        try {
            initOnce()
        } catch (e: Throwable) {
            mErrors.add(e)
            XPBridge.log(Log.getStackTraceString(e))
        }
    }
}