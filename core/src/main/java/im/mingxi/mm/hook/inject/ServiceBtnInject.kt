package im.mingxi.mm.hook.inject

import android.view.View
import im.mingxi.loader.bridge.XPBridge
import im.mingxi.miko.annotation.FunctionHookEntry
import im.mingxi.miko.hook.BaseFuncHook
import im.mingxi.miko.util.Reflex

@FunctionHookEntry(itemName = "微信服务按钮注入", itemType = FunctionHookEntry.WECHAT_ITEM)
class ServiceBtnInject : BaseFuncHook() {
    override fun initOnce(): Boolean {

        val viewClass = Reflex.loadClass("com.tencent.mm.ui.widget.listview.PullDownListView")
        val onLongClickMet = Reflex.findMethod(viewClass).setMethodName("onItemLongClick")
        
        XPBridge.hookAfter(onLongClickMet) {
                XPBridge.log("ServiceBtnInject Successful!")
            }
        }

    
        return true
    }
}
