package im.mingxi.mm.hook

import android.text.TextUtils
import android.view.View
import androidx.core.widget.doAfterTextChanged
import im.mingxi.core.databinding.DpiSettingBinding
import im.mingxi.miko.annotation.FunctionHookEntry
import im.mingxi.miko.hook.SwitchHook
import im.mingxi.miko.ui.dialog.XDialog
import im.mingxi.miko.ui.util.FuncRouter
import im.mingxi.miko.util.toAppClass
import im.mingxi.miko.util.xpcompat.XPHelpers

@FunctionHookEntry(itemType = FunctionHookEntry.WECHAT_ITEM)
class EditHintHook : SwitchHook() {
    override val name: String
        get() = "输入框显示灰字"
    override val description: CharSequence?
        get() = "重启生效，点击设置灰字"
    override val uiItemLocation: String
        get() = FuncRouter.BEAUTIFY
    override val onClick: ((View) -> Unit) = { v ->
        XDialog.create(v.context).apply {
            title = name
            val binding = DpiSettingBinding.inflate(layoutInflater)
            val editText = binding.dpiEdit
            editText.setText(mConfig.decodeString("$TAG.config.text", ""))
            editText.doAfterTextChanged { text ->
                if (!TextUtils.isEmpty(text)) {
                    mConfig.encode("$TAG.config.text", text.toString())
                }
            }
            contain(binding.root)
        }.build()
    }

    override fun initOnce(): Boolean {
        "com.tencent.mm.ui.widget.cedit.api.MMFlexEditText".toAppClass()!!.constructors.forEach {
            it.hookAfterIfEnable { param ->
                val text = mConfig.decodeString("$TAG.config.text", "")
                if (!TextUtils.isEmpty(text)) XPHelpers.callMethod(
                    param.thisObject,
                    "setHint",
                    text
                );
            }
        }
        return true
    }
}