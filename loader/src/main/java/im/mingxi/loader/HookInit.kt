package im.mingxi.loader

import im.mingxi.loader.hotpatch.HotPatch2
import im.mingxi.loader.util.PathUtil

object HookInit {
    fun doOnLoad() {
        PathUtil.appPath = XposedPackage.appInfo.dataDir + "/Miko/"
        PathUtil.apkPath = XposedPackage.appInfo.sourceDir
        PathUtil.appPath =
            "/storage/emulated/0/Android/data/" + XposedPackage.packageName + "/Miko/"
        HotPatch2.onLoad()
    }

    fun onLoad() {

    }

}
