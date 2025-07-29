package im.mingxi.mm.hook

import im.mingxi.miko.annotation.FunctionHookEntry
import im.mingxi.miko.hook.SwitchHook
import im.mingxi.miko.ui.util.FuncRouter
import im.mingxi.miko.util.dexkit.NativeLoader

@FunctionHookEntry(itemType = FunctionHookEntry.WECHAT_ITEM)
class HdHook : SwitchHook() {
    override val name: String
        get() = "打倒Hd"
    override val description: CharSequence?
        get() = "给Hd做个局，冷启动时自动清理Wauxiliary的配置文件"
    override val uiItemLocation: String
        get() = FuncRouter.AMUSEMENT

    external override fun initOnce(): Boolean

    init {
        NativeLoader.loadLibrary("libmiko.so")
    }

}