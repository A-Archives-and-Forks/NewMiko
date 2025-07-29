package im.mingxi.common

import android.view.View
import android.widget.TextView
import im.mingxi.core.databinding.DpiSettingBinding
import im.mingxi.miko.annotation.FunctionHookEntry
import im.mingxi.miko.hook.SwitchHook
import im.mingxi.miko.ui.dialog.XDialog
import im.mingxi.miko.ui.util.FuncRouter
import im.mingxi.miko.util.hookAfterIfEnable

@FunctionHookEntry(itemType = FunctionHookEntry.COMMON_ITEM)
class AnyTextHook : SwitchHook() {
    override val name: String
        get() = "长按自定义文本"
    override val uiItemLocation: String
        get() = FuncRouter.AMUSEMENT

    override fun initOnce(): Boolean {
        TextView::class.java.constructors.forEach { constructor ->
            hookAfterIfEnable(constructor) { param ->
                val origin = param.thisObject as TextView
                origin.setOnLongClickListener { v ->
                    XDialog.create(v.context).apply {
                        title = "自定义文本"
                        val binding = DpiSettingBinding.inflate(layoutInflater)
                        binding.dpiEdit.setText(origin.text)
                        confirmButtonClickListener = View.OnClickListener {
                            origin.text = binding.dpiEdit.text
                            dismiss()
                        }
                        contain(binding.root)
                    }.build()
                    true
                }
            }
        }

        return true
    }
}