package im.mingxi.loader.xp82

import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import im.mingxi.loader.HookInit
import im.mingxi.loader.XposedPackage
import im.mingxi.loader.bridge.XPBridge
import im.mingxi.loader.bridge.impl.XPBridge82Impl

class HookEntry : IXposedHookLoadPackage {
    override fun handleLoadPackage(loadPackageParam: LoadPackageParam) {
        if (loadPackageParam.isFirstApplication) {
            XPBridge.setImpl(XPBridge82Impl())
            try {
                Integer.parseInt(
                    XposedPackage.processName.substring(XposedPackage.processName.length - 1)
                )
            } catch (_: Throwable) {
                if ("LOADED" == System.getProperty("MikoLoadStatus")) {
                    return
                }
                System.setProperty("MikoLoadStatus", "LOADED")
                XposedPackage.classLoader = loadPackageParam.classLoader
                XposedPackage.appInfo = loadPackageParam.appInfo
                XposedPackage.packageName = loadPackageParam.packageName
                XposedPackage.processName = loadPackageParam.processName
                HookInit.doOnLoad()
            }
        }
    }
}