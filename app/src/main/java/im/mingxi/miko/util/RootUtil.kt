package im.mingxi.miko.util

object RootUtil {

    @JvmStatic
    fun deleteAsRoot(path: String): Boolean {
        try {
            val exec = Runtime.getRuntime().exec("su")
            val outputStream = exec.outputStream
            outputStream.write("rm -rf $path\n".toByteArray())
            outputStream.flush()
            outputStream.close()

            val exitCode = exec.waitFor()
            return (exitCode == 0)
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }
}