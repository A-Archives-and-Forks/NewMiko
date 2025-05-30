package im.mingxi.miko.proxy

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.util.Log
import java.lang.reflect.InvocationTargetException


class ProxyHandler(private val mDefault: Handler.Callback?) : Handler.Callback {
    @SuppressLint("PrivateApi", "DiscouragedPrivateApi")
    override fun handleMessage(msg: Message): Boolean {
        when (msg.what) {
            100 -> {
                try {
                    val record = msg.obj
                    val fIntent = record.javaClass.getDeclaredField("intent")
                    fIntent.isAccessible = true
                    val intent = checkNotNull(fIntent[record] as Intent)
                    // 获取bundle
                    var bundle: Bundle? = null
                    try {
                        val fExtras = Intent::class.java.getDeclaredField("mExtras")
                        fExtras.isAccessible = true
                        bundle = fExtras[intent] as Bundle
                    } catch (e: Exception) {
                    }
                    // 设置
                    if (bundle != null) {
                        bundle.classLoader = ActivityProxyManager.HostClassLoader
                        if (intent.hasExtra("随便填点，检测用的")) {
                            val rIntent = intent.getParcelableExtra<Intent>("随便填点，检测用的")
                            fIntent[record] = rIntent
                        }
                    }
                } catch (e: Exception) {
                }
            }

            159 -> {
                val clientTransaction = msg.obj
                try {
                    if (clientTransaction != null) {
                        val getCallbacksMethod =
                            Class.forName("android.app.servertransaction.ClientTransaction")
                                .getDeclaredMethod("getCallbacks")
                        getCallbacksMethod.isAccessible = true
                        val clientTransactionItems =
                            getCallbacksMethod.invoke(clientTransaction) as List<*>
                        if (clientTransactionItems == null && clientTransactionItems.isEmpty()) return false
                        for (item in clientTransactionItems) {
                            if (item != null) {
                                val clz = item::class.java
                                if (clz.name.contains("LaunchActivityItem")) {

                                    processLaunchActivityItem(clientTransaction, item)

                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                }
            }
        }
        return mDefault != null && mDefault.handleMessage(msg)
    }

    companion object {
        @Throws(
            NoSuchFieldException::class,
            IllegalAccessException::class,
            InvocationTargetException::class,
            NoSuchMethodException::class,
            ClassNotFoundException::class
        )
        private fun processLaunchActivityItem(clientTransaction: Any, item: Any) {
            val clz: Class<*> = item.javaClass
            val fmIntent = clz.getDeclaredField("mIntent")
            fmIntent.isAccessible = true
            val wrapper = fmIntent[item] as Intent
            Log.d("ParasiticsUtils:", "handleMessage: target wrapper =$wrapper")
            checkNotNull(wrapper)
            var bundle: Bundle? = null
            try {
                val fExtras = Intent::class.java.getDeclaredField("mExtras")
                fExtras.isAccessible = true
                bundle = fExtras[wrapper] as Bundle
            } catch (e: Exception) {
            }
            if (bundle != null) {
                bundle.classLoader = ActivityProxyManager.HostClassLoader
                if (wrapper.hasExtra("随便填点，检测用的")) {
                    val realIntent = wrapper.getParcelableExtra<Intent>("随便填点，检测用的")
                    fmIntent[item] = realIntent
                    if (Build.VERSION.SDK_INT >= 31) {
                        val token =
                            clientTransaction
                                .javaClass
                                .getMethod("getActivityToken")
                                .invoke(clientTransaction) as IBinder
                        val cActivityThread = Class.forName("android.app.ActivityThread")
                        val currentActivityThread =
                            cActivityThread.getDeclaredMethod("currentActivityThread")
                        currentActivityThread.isAccessible = true
                        val activityThread = checkNotNull(currentActivityThread.invoke(null))
                        try {
                            val acr =
                                activityThread
                                    .javaClass
                                    .getMethod("getLaunchingActivity", IBinder::class.java)
                                    .invoke(activityThread, token)
                            if (acr != null) {
                                val fAcrIntent = acr.javaClass.getDeclaredField("intent")
                                fAcrIntent.isAccessible = true
                                fAcrIntent[acr] = realIntent
                            }
                        } catch (e: NoSuchMethodException) {
                            if (Build.VERSION.SDK_INT == 33) {
                            } else {
                                throw e
                            }
                        }
                    }
                }
            }
        }
    }
}