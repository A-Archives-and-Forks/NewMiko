package im.mingxi.mm.hook

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.toColorInt
import androidx.core.widget.doAfterTextChanged
import com.highcapable.kavaref.KavaRef.Companion.resolve
import im.mingxi.core.databinding.TextColorBinding
import im.mingxi.loader.bridge.XPHelper
import im.mingxi.miko.annotation.FunctionHookEntry
import im.mingxi.miko.hook.SwitchHook
import im.mingxi.miko.ui.dialog.XDialog
import im.mingxi.miko.ui.util.FuncRouter
import im.mingxi.miko.util.toAppClass

@FunctionHookEntry(itemType = FunctionHookEntry.WECHAT_ITEM)
class TextColorHook : SwitchHook() {
    override val name: String
        get() = "修改聊天字体颜色"
    override val uiItemLocation: String
        get() = FuncRouter.BEAUTIFY
    override val description: CharSequence
        get() = "点击进行详细设置"
    override val onClick: ((View) -> Unit) = { v ->
        XDialog.create(v.context).apply {
            title = name
            val binding = TextColorBinding.inflate(layoutInflater)
            binding.colorText.doAfterTextChanged { text ->
                if (TextUtils.isEmpty(text)) {
                    mConfig.encode("$TAG.config.text", "")
                    return@doAfterTextChanged
                }
                mConfig.encode("$TAG.config.text", text.toString())
            }
            binding.colorLink.doAfterTextChanged { text ->
                if (TextUtils.isEmpty(text)) {
                    mConfig.encode("$TAG.config.link", "")
                    return@doAfterTextChanged
                }
                mConfig.encode("$TAG.config.link", text.toString())
            }
            contain(binding.root)
        }.build()
    }


    override fun initOnce(): Boolean {
        "com.tencent.mm.ui.widget.MMNeat7extView".toAppClass().resolve().firstMethod {
            superclass()
            parameters(
                Context::class.java, AttributeSet::class.java,
                Int::class.javaPrimitiveType!!
            )
        }.self.hookAfterIfEnable {
            if (!XPHelper.getStackData().contains("<init>")) return@hookAfterIfEnable
            val thiz = it.thisObject as View
            val textColor = mConfig.decodeString("$TAG.config.text", "")
            val linkColor = mConfig.decodeString("$TAG.config.link", "")
            if (!TextUtils.isEmpty(textColor)) {
                val method = thiz.resolve().firstMethod {
                    superclass()
                    name = "setTextColor"
                }.self
                method.isAccessible = true
                method.invoke(thiz, textColor!!.toColorInt())
            }
            if (!TextUtils.isEmpty(linkColor)) {
                val method = thiz.resolve().firstMethod {
                    superclass()
                    name = "setLinkTextColor"
                }.self
                method.isAccessible = true
                method.invoke(thiz, linkColor!!.toColorInt())
            }
        }
        return true
    }
}