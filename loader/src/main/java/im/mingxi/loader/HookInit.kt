package im.mingxi.loader

import im.mingxi.loader.hotpatch.HotPatch
import im.mingxi.loader.util.PathUtil

object HookInit {
    
    fun doOnLoad() {
        PathUtil.dataPath = XposedPackage.appInfo.dataDir + "/Miko/"
        PathUtil.apkPath = XposedPackage.appInfo.sourceDir
        PathUtil.appPath = "/storage/emulated/0/Android/data/" + XposedPackage.packageName + "/Miko/"
        HotPatch.onLoad()
    }
    
}
