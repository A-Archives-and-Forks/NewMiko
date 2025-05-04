package im.mingxi.loader

import android.content.pm.ApplicationInfo

abstract class XposedPackage {
    companion object {
        lateinit var classLoader: ClassLoader // host Loader
        lateinit var appInfo: ApplicationInfo
        lateinit var packageName: String
        lateinit var processName: String
    }
}