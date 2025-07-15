package im.mingxi.mm.hook

import android.animation.ObjectAnimator
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import im.mingxi.miko.annotation.FunctionHookEntry
import im.mingxi.miko.hook.SwitchHook
import im.mingxi.miko.ui.util.FuncRouter
import im.mingxi.miko.util.HookEnv
import im.mingxi.miko.util.toAppClass


@FunctionHookEntry(itemType = FunctionHookEntry.WECHAT_ITEM)
class ChattingAvatarRevolve(defaultEnabled: Boolean = false) : SwitchHook(defaultEnabled) {
    override val name: String
        get() = "聊天头像旋转"
    override val uiItemLocation: String
        get() = FuncRouter.BEAUTIFY

    override fun initOnce(): Boolean {
        "com.tencent.mm.ui.chatting.view.ChattingAvatarImageView".toAppClass()!!.constructors.forEach {
            it.hookAfterIfEnable { param ->
                HookEnv.hostActivity.runOnUiThread {
                    val thiz = param.thisObject as ImageView
                    val rotation = ObjectAnimator.ofFloat(thiz, "rotation", 0f, 360f)
                    rotation.setDuration(1000) // 设置动画持续时间（毫秒）
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