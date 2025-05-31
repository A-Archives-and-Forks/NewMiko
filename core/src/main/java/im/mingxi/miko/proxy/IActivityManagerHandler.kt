package im.mingxi.miko.proxy

import android.content.Intent
import androidx.core.util.Pair
import java.lang.reflect.InvocationHandler
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method


class IActivityManagerHandler(private val activityManager: Any) : InvocationHandler {
    private fun foundFirstIntentOfArgs(args: Array<Any>): Pair<Int, Intent>? {
        for (i in args.indices) {
            if (args[i] is Intent) {
                return Pair(i, args[i] as Intent)
            }
        }
        return null
    }

    @Throws(Throwable::class)
    override fun invoke(proxy: Any, method: Method, args: Array<Any>?): Any? {
        try {
            if (args != null) {
                if (method.name == "startActivity") {
                    val pair = foundFirstIntentOfArgs(args)
                    if (pair != null) {
                        val intent = pair.second
                        if (intent.component != null) {
                            val packageName = intent.component!!.packageName
                            val className = intent.component!!.className
                            if (packageName == ActivityProxyManager.HostContext!!.packageName
                                && isModuleActivity(className)
                            ) {
                                val wrapper = Intent()
                                wrapper.setClassName(
                                    intent.component!!.packageName,
                                    ActivityProxyManager.HostActivityClassName!!
                                )
                                wrapper.putExtra(
                                    "miko_activity_proxy_intent",
                                    pair.second
                                )
                                args[pair.first] = wrapper
                            }
                        }
                    }
                }
                return method.invoke(activityManager, *args)
            }
            return method.invoke(activityManager)
        } catch (e: InvocationTargetException) {
            throw e.targetException
        }
    }

    companion object {
        fun isModuleActivity(className: String?): Boolean {
            return try {
                BaseActivity::class.java.isAssignableFrom(
                    ActivityProxyManager.ModuleClassLoader!!.loadClass(
                        className
                    )
                )
            } catch (e: Exception) {
                false
            }
        }
    }
}