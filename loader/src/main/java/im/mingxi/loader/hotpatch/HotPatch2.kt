package im.mingxi.loader.hotpatch


import android.util.Log
import im.mingxi.loader.BuildConfig
import im.mingxi.loader.bridge.XPBridge
import im.mingxi.loader.util.FileUtil
import im.mingxi.loader.util.HttpUtil
import im.mingxi.loader.util.PathUtil

object HotPatch2 {
    val hotPatchPath: String = PathUtil.dataPath + "HotPatch/"
    val hotPatchAPKPath: String = hotPatchPath + "release.apk"

    fun onLoad() : Boolean {
        // 如果是调试模式直接加载模块
        if (BuildConfig.DEBUG) {
            doLoadModuleLocal()
            return true
        }
        // 拉取云端版本
        val cloudVersion = HttpUtil.sendDataRequest("\"http://miao.yuexinya.top/HotPatch/versions.txt\"")

        return true
    }
    // Load Module from local
    // Only can invoke by debug mode
    fun doLoadModuleLocal() {
        try {
            val startupClass: Class<*> = Class.forName("im.mingxi.miko.startup.StartUp")
            val initMet = startupClass.getDeclaredMethod("doLoad")
            true.also {
                initMet.isAccessible = it
            }
            initMet.invoke(null)
        } catch (err: Exception) {
            XPBridge.log(Log.getStackTraceString(err))
        }
    }

    // 拉去本地标签
    fun getSign() : String {
        return FileUtil.readFileString("${hotPatchPath}sign.data") ?: "NO_SIGN"
    }
}