package im.mingxi.miko.startup

import android.app.Activity
import android.app.Application
import android.content.Context
import com.tencent.mmkv.MMKV
import im.mingxi.loader.XposedPackage
import im.mingxi.loader.bridge.XPBridge
import im.mingxi.loader.bridge.XPBridge.HookParam
import im.mingxi.miko.startup.HookInstaller.scanAndInstall
import im.mingxi.miko.util.HookEnv
import im.mingxi.miko.util.Reflex
import im.mingxi.miko.util.dexkit.NativeLoader
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean

object StartUp {
    val isMMKVInit: AtomicBoolean = AtomicBoolean()
    val isActInit: AtomicBoolean = AtomicBoolean()

    @JvmField
    var hostType: Int = -1
    @JvmStatic
    fun doLoad() {
        HookEnv.moduleClassLoader = StartUp::class.java.classLoader

        Reflex.setHostClassLoader(XposedPackage.classLoader)
        var appClass: Class<*>? = null
        val mmClass = Reflex.loadClass("com.tencent.mm.app.Application")
        val mobileqqClass = Reflex.loadClass("com.tencent.mobileqq.qfix.QFixApplication")
        appClass = if (mobileqqClass != null) {
            hostType = 2
            mobileqqClass
        } else {
            hostType = 1
            mmClass
        }

        if (appClass == null) // 可能也许大概应该用的到这段
            appClass = Class.forName("android.app.Application")


        // hook android.app.Application.attachBaseContext
        XPBridge.hookAfter(
            Reflex.findMethod(appClass).setMethodName("attachBaseContext").get()
        ) { param: HookParam ->
            val context = param.args[0] as Context
            HookEnv.hostContext = context
            HookEnv.hostApplication = param.thisObject as Application
            ResStartUp.doLoad(context) // 重复注入资源防止部分免root框架注入资源异常
            if (!isMMKVInit.getAndSet(true)) initializeMMKV(
                context
            )
        }

        /*
 * To prevent the framework from passing the wrong class loader,
 *  we use {@link #getClassLoader()} to get the class loader.
 */
        XPBridge.hookAfter(
            Reflex.findMethod(Activity::class.java).setMethodName("onResume").get()
        ) { param: HookParam ->
            val activity = param.thisObject as Activity
            HookEnv.hostActivity = activity
            ResStartUp.doLoad(activity) // 重复注入资源防止部分免root框架注入资源异常
            if (!isActInit.getAndSet(true)) {
                val xLoader = activity.classLoader
                if (xLoader != null) {
                    HookEnv.hostClassLoader = xLoader
                    scanAndInstall()
                    XPBridge.log("Load Successful!")
                }
            }
        }
    }

    private fun initializeMMKV(ctx: Context) {
        // 由于Miko的hotPatch基于dexClassLoader并且没有传入library参数，所以必须提前加载libmmkv.so
        // 防止mmkv#initialize自载造成闪退
        NativeLoader.loadLibrary("libmmkv.so")
        val dataDir = ctx.dataDir
        val filesDir = ctx.filesDir
        val mmkvDir = File(filesDir, "Miko_MMKV")
        if (!mmkvDir.exists()) {
            mmkvDir.mkdirs()
        }
        // MMKV requires a ".tmp" cache directory, we have to create it manually
        val cacheDir = File(mmkvDir, ".tmp")
        if (!cacheDir.exists()) {
            cacheDir.mkdir()
        }
        MMKV.initialize(ctx, mmkvDir.absolutePath)
        MMKV.mmkvWithID("global_config", MMKV.MULTI_PROCESS_MODE)
        MMKV.mmkvWithID("global_cache", MMKV.MULTI_PROCESS_MODE)
    }
}