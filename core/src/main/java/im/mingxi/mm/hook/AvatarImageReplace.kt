package im.mingxi.mm.hook

import android.graphics.BitmapFactory
import im.mingxi.loader.util.PathUtil
import im.mingxi.miko.hook.SwitchHook
import im.mingxi.miko.ui.util.FuncRouter
import im.mingxi.miko.util.dexkit.DexKit
import im.mingxi.miko.util.toAppClass
import java.io.File

// @FunctionHookEntry(itemType = FunctionHookEntry.WECHAT_ITEM)
class AvatarImageReplace : SwitchHook() {
    override val name: String
        get() = "替换好友头像"
    override val uiItemLocation: String
        get() = FuncRouter.CONTACTS

    override fun initOnce(): Boolean {
        val cls = DexKit.requireClassFromCache("AvatarStorage").toAppClass()
        cls.declaredMethods.forEach {
            if (it.parameterTypes.size == 1 && it.parameterTypes[0] == String::class.java) {
                it.isAccessible = true
                val result = it.hookAfterIfEnable { param ->
                    val file = File("${PathUtil.appPath}Avatar/${param.args[0]}")
                    if (file.exists()) {
                        param.result = BitmapFactory.decodeFile(file.absolutePath)
                    }
                }
                return true
            }
        }
        return true
    }
}