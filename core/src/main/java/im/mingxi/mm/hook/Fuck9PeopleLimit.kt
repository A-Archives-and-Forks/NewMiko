package im.mingxi.mm.hook

import android.app.Activity
import com.highcapable.kavaref.KavaRef.Companion.resolve
import im.mingxi.miko.annotation.FunctionHookEntry
import im.mingxi.miko.hook.SwitchHook
import im.mingxi.miko.ui.util.FuncRouter
import im.mingxi.miko.util.toAppClass

@FunctionHookEntry(itemType = FunctionHookEntry.WECHAT_ITEM)
class Fuck9PeopleLimit : SwitchHook() {
    override val name: String
        get() = "去除转发9人限制"
    override val uiItemLocation: String
        get() = FuncRouter.CONTACTS

    override fun initOnce(): Boolean {
        "com.tencent.mm.ui.mvvm.MvvmContactListUI".toAppClass().resolve().firstMethod {
            name = "onCreate"
        }.self.hookBeforeIfEnable { param ->
            (param.thisObject as Activity).intent.putExtra("max_limit_num", 999)
        }
        return true
    }
}