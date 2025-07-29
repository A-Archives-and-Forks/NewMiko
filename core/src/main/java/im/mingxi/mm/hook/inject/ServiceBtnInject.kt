package im.mingxi.mm.hook.inject

import android.widget.AdapterView
import im.mingxi.miko.annotation.FunctionHookEntry
import im.mingxi.miko.controller.HomeController
import im.mingxi.miko.hook.SwitchHook
import im.mingxi.miko.util.Reflex.findMethod
import im.mingxi.miko.util.Reflex.findMethodObj
import im.mingxi.miko.util.hookAfterIfEnable
import im.mingxi.mm.struct.pullDownListViewClass

@FunctionHookEntry(itemName = "微信服务按钮注入", itemType = FunctionHookEntry.WECHAT_ITEM)
class ServiceBtnInject(
    override val name: String = "允许长按服务进入菜单",
    override val uiItemLocation: String =
        "模块设置及调试"
) : SwitchHook(defaultEnabled = false) {
    override fun initOnce(): Boolean {

        hookAfterIfEnable(
            findMethod(pullDownListViewClass).setMethodName("onItemLongClick").get()
        ) {
            val adapterView = it.args[0] as AdapterView<*>
            val adapter = adapterView.adapter
            val position = it.args[2] as Int
            val preference = findMethodObj(adapter).setMethodName("getItem").get()
            val preferenceInst = preference.invoke(adapter, position)
            if (preferenceInst != null) {
                if ("服务" == preferenceInst.toString()) {
                    HomeController.openHomeActivity()
                }
            }
        }
        return true
    }
}
