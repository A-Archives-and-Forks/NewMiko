package im.mingxi.miko.proxy


import android.app.Instrumentation
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Handler
import java.lang.reflect.Field
import java.lang.reflect.Proxy
import java.util.concurrent.atomic.AtomicBoolean


class ActivityProxyManager {
    companion object {
        @JvmStatic
        val TAG: String = "ActivityProxyManager(活动代理管理器)"
        val Initialized: AtomicBoolean = AtomicBoolean()

        var ResId: Int = 0

        var ModuleApkPath: String? = null

        var HostContext: Context? = null

        var HostActivityClassName: String? = null

        var ModuleClassLoader: ClassLoader? = null

        var HostClassLoader: ClassLoader? = null

        fun initActivityProxyManager(
            hostContext: Context, ModuleApkPath: String?, Resid: Int
        ) {
            var ModuleApkPath = ModuleApkPath
            if (ResId != 0) ResId = Resid

            HostClassLoader = hostContext.classLoader
            ModuleClassLoader = ActivityProxyManager::class.java.classLoader
            ModuleApkPath = ModuleApkPath
            HostContext = hostContext

            HostActivityClassName = getAllActivity(hostContext)!![0].name
            if (Initialized.getAndSet(true)) return
            try {
                val cActivityThread = Class.forName("android.app.ActivityThread")
                // 获取sCurrentActivityThread对象
                val fCurrentActivityThread: Field =
                    cActivityThread.getDeclaredField("sCurrentActivityThread")
                fCurrentActivityThread.setAccessible(true)
                val currentActivityThread: Any = fCurrentActivityThread.get(null)

                replaceInstrumentation(currentActivityThread)
                replaceHandler(currentActivityThread)
                replaceIActivityManager()
                try {
                    replaceIActivityTaskManager()
                } catch (ignored: Exception) {
                }
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }

        fun getAllActivity(context: Context): Array<ActivityInfo>? {
            val packageManager = context.packageManager
            val packageInfo: PackageInfo
            try {
                packageInfo =
                    packageManager.getPackageInfo(
                        context.packageName,
                        PackageManager.GET_ACTIVITIES
                    )
                val activities = packageInfo.activities
                return activities
            } catch (e: PackageManager.NameNotFoundException) {
                throw RuntimeException(e)
            }
        }

        @Throws(Exception::class)
        private fun replaceInstrumentation(activityThread: Any) {
            val fInstrumentation: Field =
                activityThread.javaClass.getDeclaredField("mInstrumentation")
            fInstrumentation.setAccessible(true)
            fInstrumentation.set(
                activityThread,
                ProxyInstrumentation(fInstrumentation.get(activityThread) as Instrumentation)
            )
        }

        @Throws(Exception::class)
        private fun replaceHandler(activityThread: Any) {
            val fHandler: Field = activityThread.javaClass.getDeclaredField("mH")
            fHandler.setAccessible(true)
            val mHandler: Handler = fHandler.get(activityThread) as Handler

            val chandler = Class.forName("android.os.Handler")
            val fCallback: Field = chandler.getDeclaredField("mCallback")
            fCallback.setAccessible(true)
            val mCallback: Handler.Callback = fCallback.get(mHandler) as Handler.Callback
            fCallback.set(mHandler, ProxyHandler(mCallback))
        }

        @Throws(Exception::class)
        private fun replaceIActivityManager() {
            var activityManagerClass: Class<*>
            var gDefaultField: Field
            try {
                activityManagerClass = Class.forName("android.app.ActivityManagerNative")
                gDefaultField = activityManagerClass.getDeclaredField("gDefault")
            } catch (err1: Exception) {
                try {
                    activityManagerClass = Class.forName("android.app.ActivityManager")
                    gDefaultField =
                        activityManagerClass.getDeclaredField("IActivityManagerSingleton")
                } catch (err2: Exception) {
                    return
                }
            }
            gDefaultField.setAccessible(true)
            val gDefault: Any = gDefaultField.get(null)
            val singletonClass = Class.forName("android.util.Singleton")
            val mInstanceField: Field = singletonClass.getDeclaredField("mInstance")
            mInstanceField.setAccessible(true)
            val mInstance: Any = mInstanceField.get(gDefault)
            val amProxy: Any =
                Proxy.newProxyInstance(
                    ModuleClassLoader,
                    arrayOf<Class<*>>(Class.forName("android.app.IActivityManager")),
                    IActivityManagerHandler(mInstance)
                )
            mInstanceField.set(gDefault, amProxy)
        }

        @Throws(Exception::class)
        private fun replaceIActivityTaskManager() {
            val activityTaskManagerClass = Class.forName("android.app.ActivityTaskManager")
            val fIActivityTaskManagerSingleton: Field =
                activityTaskManagerClass.getDeclaredField("IActivityTaskManagerSingleton")
            fIActivityTaskManagerSingleton.setAccessible(true)
            val singleton: Any = fIActivityTaskManagerSingleton.get(null)
            var activityManagerClass: Class<*>
            var gDefaultField: Field
            try {
                activityManagerClass = Class.forName("android.app.ActivityManagerNative")
                gDefaultField = activityManagerClass.getDeclaredField("gDefault")
            } catch (err1: Exception) {
                try {
                    activityManagerClass = Class.forName("android.app.ActivityManager")
                    gDefaultField =
                        activityManagerClass.getDeclaredField("IActivityManagerSingleton")
                } catch (err2: Exception) {
                    return
                }
            }
            gDefaultField.setAccessible(true)
            val gDefault: Any = gDefaultField.get(null)
            val singletonClass = Class.forName("android.util.Singleton")
            val mInstanceField: Field = singletonClass.getDeclaredField("mInstance")
            mInstanceField.setAccessible(true)
            singletonClass.getMethod("get").invoke(singleton)
            val mDefaultTaskMgr: Any = mInstanceField.get(singleton)
            val proxy2: Any =
                Proxy.newProxyInstance(
                    ModuleClassLoader,
                    arrayOf<Class<*>>(Class.forName("android.app.IActivityTaskManager")),
                    IActivityManagerHandler(mDefaultTaskMgr)
                )
            mInstanceField.set(singleton, proxy2)
        }
    }
}