package im.mingxi.mm.hook.inject

import android.view.View
import im.mingxi.loader.bridge.XPBridge
import im.mingxi.miko.annotation.FunctionHookEntry
import im.mingxi.miko.hook.BaseFuncHook
import im.mingxi.miko.util.Reflex

@FunctionHookEntry(itemName = "mm加号菜单注入", itemType = FunctionHookEntry.WECHAT_ITEM)
class PlusMenuInject : BaseFuncHook() {
    override fun initOnce(): Boolean {

        val viewClass = Reflex.loadClass("com.tencent.mm.ui.HomeUI\$PlusActionView")
        val constructor = viewClass.declaredConstructors[0]

        XPBridge.hookAfter(constructor) {
            val viewInst: View = it.thisObject as View
            viewInst.setOnLongClickListener {
                XPBridge.log("PlusMenuInject Successful!")
                true
            }
        }

        return true
    }
}