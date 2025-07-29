package im.mingxi.miko.startup

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import im.mingxi.loader.bridge.XPBridge
import im.mingxi.loader.util.Constants
import im.mingxi.loader.util.PathUtil
import im.mingxi.miko.hook.BaseComponentHook
import im.mingxi.miko.hook.BaseFuncHook
import im.mingxi.miko.ui.dialog.ProcessDialog
import im.mingxi.miko.util.HookEnv
import im.mingxi.miko.util.SignUtil
import im.mingxi.miko.util.Util
import im.mingxi.miko.util.config
import im.mingxi.miko.util.dexkit.DexKit
import im.mingxi.miko.util.dexkit.IFinder
import im.mingxi.miko.util.dexkit.NativeLoader
import im.mingxi.miko.util.dexkit.OFinder
import im.mingxi.miko.util.toAppClass
import im.mingxi.mm.api.AutoFinder
import im.mingxi.mm.manager.impl.MMEnvManagerImpl
import im.mingxi.mm.manager.impl.WeChatContactStorageImpl.Companion.conversationStorage
import im.mingxi.mm.manager.impl.WeChatContactStorageImpl.Companion.sqliteDB
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import org.luckypray.dexkit.DexKitBridge
import java.util.concurrent.atomic.AtomicBoolean

object HookInstaller {
    private val isInit: AtomicBoolean = AtomicBoolean()
    val mItems = ArrayList<BaseFuncHook>()
    val uiList = ArrayList<BaseComponentHook>()

    init {
        NativeLoader.loadLibrary("libdexkit.so")
    }

    fun scanAndInstall() {
        if (!isInit.getAndSet(true)) {
            Thread {
                // 先获取所有hook条目
                val resultMet = Class.forName(
                    "im.mingxi.miko.annotation.result.FunctionHookEntryResult"
                ).getDeclaredMethod("getAnnotatedFunctionHookEntryList")
                resultMet.isAccessible = true
                val allItems = resultMet.invoke(null) as Array<BaseFuncHook>
                val loadType = StartUp.hostType

                // 筛选所有可用的hook条目
                allItems.forEach {
                    when (loadType) {
                        0 -> mItems.add(it)
                        1 -> if (it.TAG.contains("mm")) mItems.add(it)
                        2 -> if (it.TAG.contains("mobileqq")) mItems.add(it)

                    }
                }

                if (config.decodeString("Miko.AllConfig", "NO_SIGN") != SignUtil.sign) {

                    showProcessDialog()
                    val dexKit = DexKitBridge.create(PathUtil.apkPath!!)
                    dexKit.use {
                        if (Util.isWeChat()) AutoFinder.onLoad(dexKit)

                        mItems.forEachIndexed { i, item ->
                            if (item is IFinder) {
                                sendMessageToDialog(
                                    "正在加载核心类[${i + 1}/${mItems.size}]${item.TAG}"
                                )
                                val thiz = item as IFinder
                                try {
                                    thiz.dexFind(dexKit)
                                } catch (e: Exception) {
                                    item.mErrors.add(e)
                                    XPBridge.log(Log.getStackTraceString(e))
                                }
                            }
                        }

                    }
                    config.encode("Miko.AllConfig", SignUtil.sign)
                    HookEnv.hostActivity.runOnUiThread {
                        mDialog.dismiss()
                    }
                }

                // 挂起一些需要抢时间的钩子
                if (config.decodeString("Miko.AllConfig", "NO_SIGN") == SignUtil.sign) {
                    DexKit.requireClassFromCache("ConversationStorage")
                        .toAppClass().declaredConstructors.forEach {
                            XPBridge.hookAfter(it) { param ->
                                conversationStorage = param.thisObject
                                sqliteDB = param.args[0]
                            }
                        }
                }


                // 筛选ui条目
                mItems.forEach {
                    if (it is BaseComponentHook) uiList.add(it)
                    if (it is OFinder) it.onInstance()
                    // 加载可用条目
                    it.initialize()
                }
            }.start()

            OkHttpClient().newCall(
                Request.Builder()
                    .url("${Constants.mWeb}/Api/getUserData?uin=${MMEnvManagerImpl().getWxId()}")
                    .build()
            ).enqueue(object : Callback {
                override fun onFailure(call: Call, e: java.io.IOException) {
                    XPBridge.log(e.message)
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
                        val tempUser = JSONObject(response.body?.string())
                        if (tempUser.optInt("code") == 200) {
                            System.setProperty("Miko.isLogin", "true")
                            System.setProperty("Miko.userData", tempUser.toString())
                        }
                    }
                }
            })
        }
    }

    @SuppressLint("StaticFieldLeak")
    private lateinit var mDialog: ProcessDialog

    private fun showProcessDialog() {
        HookEnv.hostActivity.runOnUiThread {
            val dialog =
                ProcessDialog(
                    HookEnv.hostActivity as Context,
                    "正在启动核心加载器"
                )
            dialog.show()
            mDialog = dialog
        }
    }

    fun sendMessageToDialog(msg: String) {
        HookEnv.hostActivity.runOnUiThread {
            mDialog.textView.text = msg
        }
    }


}