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
    fun isRoot(): Boolean {
        try {
            val exec = Runtime.getRuntime().exec("su")
            val outputStream = exec.outputStream
            outputStream.write("exit\n".toByteArray())
            outputStream.flush()
            outputStream.close()
            val z = exec.waitFor() == 0
            exec.destroy()
            return z
        } catch (unused: Throwable) {
            return false
        }
    }
}