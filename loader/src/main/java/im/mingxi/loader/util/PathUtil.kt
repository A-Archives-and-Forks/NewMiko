package im.mingxi.loader.util

class PathUtil private constructor() {
    init {
        throw RuntimeException("No instance for you!")
    }

    companion object {
        @JvmField
        var apkPath: String? = null // 宿主应用路径

        @JvmField
        var moduleApkPath: String? = null // 模块应用路径

        @JvmField
        var appPath: String? = null

        @JvmField
        var dataPath: String? = null
    }
}