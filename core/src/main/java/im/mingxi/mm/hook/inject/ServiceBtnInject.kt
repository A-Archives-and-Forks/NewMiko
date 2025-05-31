package im.mingxi.mm.hook.inject

import im.mingxi.miko.annotation.FunctionHookEntry
import im.mingxi.miko.hook.BaseFuncHook

@FunctionHookEntry(itemName = "微信服务按钮注入", itemType = FunctionHookEntry.WECHAT_ITEM)
class ServiceBtnInject : BaseFuncHook() {
    override fun initOnce(): Boolean {


        return true
    }
}
