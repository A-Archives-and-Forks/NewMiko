package im.mingxi.mm.hook

import android.animation.ObjectAnimator
import android.text.InputType
import android.text.TextUtils
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import androidx.core.widget.doAfterTextChanged
import im.mingxi.core.databinding.DpiSettingBinding
import im.mingxi.miko.annotation.FunctionHookEntry
import im.mingxi.miko.hook.SwitchHook
import im.mingxi.miko.ui.dialog.XDialog
import im.mingxi.miko.ui.util.FuncRouter
import im.mingxi.miko.util.HookEnv
import im.mingxi.miko.util.toAppClass


@FunctionHookEntry(itemType = FunctionHookEntry.WECHAT_ITEM)
class ChattingAvatarRevolve(defaultEnabled: Boolean = false) : SwitchHook(defaultEnabled) {
    override val name: String
        get() = "聊天头像旋转"
    override val uiItemLocation: String
        get() = FuncRouter.BEAUTIFY
    override val description: CharSequence?
        get() = "点击进行详细设置"
    override val onClick: ((View) -> Unit)?
        get() = { v ->
            XDialog.create(app = v.context).apply {
                title = "设置头像旋转速度(单位毫秒)"
                val binding = DpiSettingBinding.inflate(layoutInflater)
                binding.dpiEdit.inputType = InputType.TYPE_CLASS_NUMBER
                binding.dpiEdit.setText(mConfig.decodeInt("$TAG.config.speed", 1000).toString())
                binding.dpiEdit.doAfterTextChanged { text ->
                    if (!TextUtils.isEmpty(text)) {
                        mConfig.encode("$TAG.config.speed", text.toString().toInt())
                    }
                }
                contain(binding.root)

            }.build()
        }

    override fun initOnce(): Boolean {
        "com.tencent.mm.ui.chatting.view.ChattingAvatarImageView".toAppClass()!!.constructors.forEach {
            it.hookAfterIfEnable { param ->
                HookEnv.hostActivity.runOnUiThread {
                    val thiz = param.thisObject as ImageView
                    val rotation = ObjectAnimator.ofFloat(thiz, "rotation", 0f, 360f)
                    rotation.setDuration(
                        mConfig.decodeInt("$TAG.config.speed", 1000).toLong()
                    ) // 设置动画持续时间（毫秒）
                    rotation.repeatCount = ObjectAnimator.INFINITE // 设置无限重复
                    rotation.interpolator = LinearInterpolator() // 设置插值器为线性，使旋转匀速
                    // 启动动画
                    rotation.start()
                }
            }
        }
        return true
    }
}