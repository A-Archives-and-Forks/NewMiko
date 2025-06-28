package im.mingxi.loader.hotpatch


import android.util.Log
import dalvik.system.DexClassLoader
import im.mingxi.loader.bridge.XPBridge
import im.mingxi.loader.util.Constants
import im.mingxi.loader.util.FileUtil
import im.mingxi.loader.util.HttpUtil
import im.mingxi.loader.util.HttpUtil.sendDataRequest
import im.mingxi.loader.util.PathUtil
import java.io.File

object HotPatch {
    val hotPatchPath: String = PathUtil.dataPath + "HotPatch/"
    val hotPatchAPKPath: String = hotPatchPath + "release.apk"

    fun onLoad(): Boolean {
        if (!Constants.isHotPatch) {
            this.doLoadModuleLocal()
            return true
        }
        // 拉取云端版本
        val cloudVersionSign =
            sendDataRequest("\"http://miao.yuexinya.top/HotPatch/versions.txt\"") ?: return false
        if (this.getSign() == cloudVersionSign) { //签名正确则加载模块
            this.doLoadModuleCloud()
            return true
        }
        val file: File = File(hotPatchAPKPath)
        if (!file.exists()) file.createNewFile()
        HttpUtil.downloadToFile(
            "http://miao.yuexinya.top/HotPatch/release.apk", hotPatchAPKPath
        )
        val signFile = File("${hotPatchPath}sign.data")
        if (!signFile.exists()) signFile.createNewFile()
        signFile.writeText(cloudVersionSign)
        /*懒得获取Context来调用 {@link im.mingxi.loader.util.ActivityUtil#killAppProcess(Context)}，所以我们暴力点*/
        System.exit(0)


        return false
    }

    // Load Module from local
    // Only can invoke by debug mode
    private fun doLoadModuleLocal() {
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

    // 拉取本地标签
    private fun getSign(): String {
        return FileUtil.readFileString("${hotPatchPath}sign.data") ?: "NO_SIGN"
    }

    // 加载从云端下载的dex并启动StartUp
    // 由StartUp自行完成res和so的加载
    private fun doLoadModuleCloud() {
        val dexClassLoader =
            DexClassLoader(
                hotPatchAPKPath,
                hotPatchPath + "Optimized",
                null,
                HotPatch.javaClass.classLoader
            )
        try {
            val startupClass = dexClassLoader.loadClass("im.mingxi.miko.startup.StartUp")
            val initMet = startupClass.getDeclaredMethod("doLoad")
            initMet.isAccessible = true
            initMet.invoke(null)
        } catch (err: java.lang.Exception) {
            XPBridge.log(Log.getStackTraceString(err))
        }
    }
}