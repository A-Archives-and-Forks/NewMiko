package im.mingxi.loader.util

import android.app.ActivityManager
import android.content.Context
import android.os.Process

object ActivityUtil {
    fun killAppProcess(context: Context) {
        // 注意：不能先杀掉主进程，否则逻辑代码无法继续执行，需先杀掉相关进程最后杀掉主进程
        val mActivityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val mList = mActivityManager.runningAppProcesses
        for (runningAppProcessInfo in mList) {
            if (runningAppProcessInfo.pid != Process.myPid()) {
                Process.killProcess(runningAppProcessInfo.pid)
            }
        }
        Process.killProcess(Process.myPid())
        System.exit(0)
    }
}