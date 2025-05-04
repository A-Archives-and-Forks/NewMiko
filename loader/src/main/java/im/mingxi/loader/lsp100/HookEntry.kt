package im.mingxi.loader.lsp100

import im.mingxi.loader.HookInit
import im.mingxi.loader.XposedPackage
import im.mingxi.loader.bridge.XPBridge
import im.mingxi.loader.lsp100.impl.LSPBridge100Impl
import im.mingxi.loader.util.PathUtil
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface

class HookEntry(
    xposedInterface: XposedInterface,
    moduleLoadedParam: XposedModuleInterface.ModuleLoadedParam
) : XposedModule(xposedInterface, moduleLoadedParam) {

    init {
        XPBridge.setImpl(LSPBridge100Impl(xposedInterface))
        XposedPackage.processName = moduleLoadedParam.processName
        PathUtil.moduleApkPath = xposedInterface.applicationInfo.sourceDir
    }

    override fun onPackageLoaded(packageLoadedParam: XposedModuleInterface.PackageLoadedParam) {
        super.onPackageLoaded(packageLoadedParam)
        try {
            Integer.parseInt(
                XposedPackage.processName.substring(XposedPackage.processName.length - 1)
            )
        } catch (_: Throwable) {
            if ("LOADED" == System.getProperty("MikoLoadStatus")) {
                return
            }
            System.setProperty("MikoLoadStatus", "LOADED")
            if (packageLoadedParam.isFirstPackage) {
                XposedPackage.classLoader = packageLoadedParam.classLoader
                XposedPackage.appInfo = packageLoadedParam.applicationInfo
                XposedPackage.packageName = packageLoadedParam.packageName
                HookInit.doOnLoad()
            }
        }
    }
}