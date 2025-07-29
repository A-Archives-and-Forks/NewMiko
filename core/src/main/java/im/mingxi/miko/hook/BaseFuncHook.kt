package im.mingxi.miko.hook

import android.util.Log
import com.tencent.mmkv.MMKV
import im.mingxi.loader.bridge.XPBridge
import im.mingxi.loader.bridge.XPBridge.HookCallback
import im.mingxi.miko.util.HookEnv
import im.mingxi.miko.util.hookAfterIfEnable
import im.mingxi.miko.util.hookBeforeIfEnable
import java.lang.reflect.Member

abstract class BaseFuncHook(val defaultEnabled: Boolean = false) {
    val TAG: String = this.javaClass.name
    val simpleTAG: String = this.javaClass.simpleName
    val mErrors: ArrayList<Throwable> = ArrayList()
    var isInitialize: Boolean = false
    val mConfig = MMKV.mmkvWithID("global_config")
    val cache = MMKV.mmkvWithID("global_cache")
    val loader = HookEnv.hostClassLoader

    @Throws(Throwable::class)
    abstract fun initOnce(): Boolean

    fun isEnabled(): Boolean {
        return mConfig.decodeBool(TAG, defaultEnabled)
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

    fun unInitialize() {
        if (!isInitialize) return
        this.isInitialize = false
        mConfig.encode(TAG, false)
    }

    fun Member.hookBeforeIfEnable(callback: HookCallback) =
        this.hookBeforeIfEnable(this@BaseFuncHook, callback)

    fun Member.hookAfterIfEnable(callback: HookCallback) =
        this.hookAfterIfEnable(this@BaseFuncHook, callback)

    fun d(msg: Any): Any {
        return msg.also { XPBridge.log(it.toString()) }
    }

    fun Any.d() {
        this.also { XPBridge.log(it.toString()) }
    }

}