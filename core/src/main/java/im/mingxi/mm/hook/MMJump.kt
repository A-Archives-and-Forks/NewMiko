package im.mingxi.mm.hook

import android.content.ComponentName
import android.content.Intent
import android.view.View
import im.mingxi.core.databinding.MmjumpBinding
import im.mingxi.miko.annotation.FunctionHookEntry
import im.mingxi.miko.hook.CommonHook
import im.mingxi.miko.ui.dialog.XDialog
import im.mingxi.miko.ui.util.FuncRouter
import im.mingxi.miko.ui.widget.MikoToast
import im.mingxi.miko.util.HookEnv


@FunctionHookEntry(itemType = FunctionHookEntry.WECHAT_ITEM)
class MMJump : CommonHook() {
    override val name: String
        get() = "跳转会话"
    override val uiItemLocation: String
        get() = FuncRouter.CONTACTS
    override val onClick: ((View) -> Unit)?
        get() = { v ->
            XDialog.create(v.context).apply {
                title = name
                val binding = MmjumpBinding.inflate(layoutInflater)
                confirmButtonClickListener = View.OnClickListener {
                    val text = binding.jumpEdit.text.toString()
                    if (text.isNotEmpty()) {
                        MikoToast.makeToast(HookEnv.hostActivity, "已尝试跳转会话")
                        // 群
                        if (text.contains("@")) {
                            val intent = Intent()
                            intent.setComponent(
                                ComponentName(
                                    app, "com.tencent.mm.plugin.profile.ui.ContactInfoUI"
                                )
                            )
                            intent.putExtra("Contact_User", text.trim())
                            app.startActivity(intent)
                        } else { // 联系人
                            val intent = Intent()
                            intent.setComponent(
                                ComponentName(
                                    app, "com.tencent.mm.plugin.profile.ui.ContactInfoUI"
                                )
                            )
                            intent.putExtra("Contact_User", text.trim())
                            app.startActivity(intent)
                        }
                    } else {
                        MikoToast.makeToast(HookEnv.hostActivity, "输入不能为空")
                    }

                    dismiss()
                }
                contain(binding.root)
            }.build()
        }

    override fun initOnce(): Boolean {
        return true
    }

}