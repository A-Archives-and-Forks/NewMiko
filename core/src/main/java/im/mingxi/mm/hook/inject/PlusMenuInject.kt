package im.mingxi.mm.hook.inject

import android.view.View
import im.mingxi.miko.annotation.FunctionHookEntry
import im.mingxi.miko.controller.HomeController
import im.mingxi.miko.hook.SwitchHook
import im.mingxi.miko.util.Reflex
import im.mingxi.miko.util.hookAfterIfEnable

@FunctionHookEntry(itemName = "mm加号菜单注入", itemType = FunctionHookEntry.WECHAT_ITEM)
class PlusMenuInject : SwitchHook(defaultEnabled = true) {
    override val name: String = "注入加号菜单长按"
    override val uiItemLocation: Array<String>
        get() = arrayOf("模块设置及调试", "注入")

    override fun initOnce(): Boolean {

        val viewClass = Reflex.loadClass("com.tencent.mm.ui.HomeUI\$PlusActionView")
        val constructor = viewClass.declaredConstructors[0]

        hookAfterIfEnable(constructor) {
            val viewInst: View =
                Reflex.findMethodObj(it.thisObject).setReturnType(View::class.java).get()
                    .invoke(it.thisObject) as View
            viewInst.setOnLongClickListener {
                HomeController.openHomeActivity()
                true
            }
        }

        return true
    }
}