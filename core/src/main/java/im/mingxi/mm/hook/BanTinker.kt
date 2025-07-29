package im.mingxi.mm.hook

import android.annotation.SuppressLint
import android.content.Context
import com.highcapable.kavaref.KavaRef.Companion.resolve
import im.mingxi.loader.XposedPackage
import im.mingxi.loader.util.FileUtil
import im.mingxi.miko.annotation.FunctionHookEntry
import im.mingxi.miko.hook.SwitchHook
import im.mingxi.miko.ui.util.FuncRouter
import im.mingxi.miko.util.toAppClass
import java.io.File
import java.lang.reflect.Method

@FunctionHookEntry(itemType = FunctionHookEntry.WECHAT_ITEM)
class BanTinker : SwitchHook() {
    override val name: String
        get() = "禁用热更新"
    override val uiItemLocation: String
        get() = FuncRouter.EXPERIMENTAL

    private lateinit var isTinkerEnableWithSharedPreferences: Method
    private lateinit var isTinkerEnabled: Method


    @SuppressLint("SdCardPath")
    override fun initOnce(): Boolean {
        val cls = "com.tencent.tinker.loader.shareutil.ShareTinkerInternals".toAppClass()
        this.isTinkerEnableWithSharedPreferences = cls.resolve().firstMethod {
            name = "isTinkerEnableWithSharedPreferences"
            parameters(Context::class.java)
        }.self
        this.isTinkerEnabled = cls.resolve().firstMethod {
            name = "isTinkerEnabled"
            parameters(Integer.TYPE)
        }.self

        this.isTinkerEnableWithSharedPreferences.hookBeforeIfEnable { param ->
            param.resultFalse()
        }

        this.isTinkerEnabled.hookBeforeIfEnable { param ->
            param.resultFalse()
        }

        val file = File("/data/data/${XposedPackage.packageName}/tinker")
        if (file.exists()) {
            FileUtil.deleteFile(file)
        }

        return true
    }
}