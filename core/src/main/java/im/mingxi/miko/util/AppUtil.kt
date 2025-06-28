package im.mingxi.miko.util

import android.content.Context


object AppUtil {
    @Suppress("deprecation")
    fun getVersionCode(context: Context): Int {
        try {
            val ver = context.packageManager.getPackageInfo(context.packageName, 0).versionCode
            return ver
        } catch (_: Throwable) {
            throw AssertionError("Can not get PackageInfo!")
        }
    }
}