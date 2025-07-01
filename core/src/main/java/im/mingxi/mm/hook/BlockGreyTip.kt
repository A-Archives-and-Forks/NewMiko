package im.mingxi.mm.hook

import android.view.View
import com.highcapable.kavaref.KavaRef.Companion.resolve
import im.mingxi.loader.bridge.XPHelper
import im.mingxi.miko.annotation.FunctionHookEntry
import im.mingxi.miko.hook.SwitchHook
import im.mingxi.miko.util.toAppClass

@FunctionHookEntry(itemName = "屏蔽灰色提示", itemType = FunctionHookEntry.WECHAT_ITEM)
class BlockGreyTip : SwitchHook() {
    override val name: String
        get() = "屏蔽灰色提示"
    override val uiItemLocation: Array<String>
        get() = arrayOf("聊天", "消息")

    override fun initOnce(): Boolean {
        val met = "com.tencent.mm.view.x2c.X2CTextView"
            .toAppClass()!!
            .resolve()
            .firstMethod {
                superclass()
                name = "setText"
            }.self
            .hookAfterIfEnable {
                val view = it.thisObject as View
                if (XPHelper.getStackData().contains("recyclerview.WxRecyclerAdapter"))
                    view.visibility = View.GONE
            }
        return true
    }
}