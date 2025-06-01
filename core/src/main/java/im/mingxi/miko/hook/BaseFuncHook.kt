package im.mingxi.miko.hook

import android.util.Log
import com.tencent.mmkv.MMKV
import im.mingxi.loader.bridge.XPBridge

abstract class BaseFuncHook(val defaultEnabled: Boolean = false) {
    val TAG: String = this.javaClass.name
    val mErrors: ArrayList<Throwable> = ArrayList()
    var isInitialize: Boolean = false
    val config = MMKV.mmkvWithID("global_config")
    val cache = MMKV.mmkvWithID("global_cache")

    @Throws(Throwable::class)
    abstract fun initOnce(): Boolean

    fun isEnabled(): Boolean {
        return config.decodeBool(TAG, defaultEnabled)
    }

    fun initialize() {
        if (isInitialize) return
        if (!isEnabled()) return
        this.isInitialize = true

        try {
            initOnce()
        } catch (e: Throwable) {
            mErrors.add(e)
            XPBridge.log(Log.getStackTraceString(e))
        }
    }
}