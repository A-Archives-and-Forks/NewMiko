package im.mingxi.common

import im.mingxi.miko.annotation.FunctionHookEntry
import im.mingxi.miko.hook.SwitchHook
import im.mingxi.miko.ui.util.FuncRouter
import im.mingxi.miko.ui.widget.MikoToast
import im.mingxi.miko.util.HookEnv

@FunctionHookEntry(itemType = FunctionHookEntry.COMMON_ITEM)
class ToastAfterLoadSuccessful : SwitchHook(true) {
    override val name: String
        get() = "加载后Toast提醒"
    override val uiItemLocation: String
        get() = FuncRouter.MODULE_SETTINGS_AND_DEBUG

    override fun initOnce(): Boolean {
        MikoToast.makeToast(HookEnv.hostActivity, "Miko：插件加载成功")
        return true
    }

}