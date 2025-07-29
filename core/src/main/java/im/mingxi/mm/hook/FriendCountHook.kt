package im.mingxi.mm.hook

import android.text.InputType
import android.view.View
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import com.highcapable.kavaref.KavaRef.Companion.resolve
import im.mingxi.core.databinding.DpiSettingBinding
import im.mingxi.loader.bridge.XPHelper
import im.mingxi.miko.annotation.FunctionHookEntry
import im.mingxi.miko.hook.SwitchHook
import im.mingxi.miko.ui.dialog.XDialog
import im.mingxi.miko.ui.util.FuncRouter

@FunctionHookEntry(itemType = FunctionHookEntry.WECHAT_ITEM)
class FriendCountHook : SwitchHook() {
    override val name: String
        get() = "自定义好友数量"
    override val uiItemLocation: String
        get() = FuncRouter.CONTACTS
    override val description: CharSequence
        get() = "点击进行详细设置"
    override val onClick: ((View) -> Unit)?
        get() = { v ->
            XDialog.create(v.context).apply {
                title = "自定义好友数量(填0为不显示好友数量视图)"
                val binding = DpiSettingBinding.inflate(layoutInflater)
                binding.dpiEdit.inputType = InputType.TYPE_CLASS_NUMBER
                binding.dpiEdit.setText(mConfig.decodeInt("$TAG.config.count", 0).toString())
                binding.dpiEdit.doAfterTextChanged {
                    if (!it.isNullOrEmpty()) {
                        mConfig.encode("$TAG.config.count", it.toString().toInt())
                    }
                }
                contain(binding.root)
            }.build()
        }

    override fun initOnce(): Boolean {
        TextView::class.resolve().firstMethod {
            name = "setText"
        }.self.hookBeforeIfEnable { param ->
            if (param.args[0] != null) {
                if (param.args[0].toString().contains("个朋友") && XPHelper.getStackData()
                        .contains("com.tencent.mm.ui.contact")
                ) {
                    val count = mConfig.decodeInt("$TAG.config.count", 0)
                    if (count == 0) {
                        val thiz = param.thisObject as TextView
                        thiz.visibility = View.GONE
                    } else {
                        param.args[0] = "${count}个朋友"
                    }
                }
            }
        }
        return true
    }

}