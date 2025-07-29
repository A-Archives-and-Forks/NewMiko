package im.mingxi.mm.hook

import android.view.View
import im.mingxi.miko.hook.SwitchHook
import im.mingxi.miko.ui.util.FuncRouter
import im.mingxi.miko.util.toAppClass

// @FunctionHookEntry(itemType = FunctionHookEntry.WECHAT_ITEM)
class ConversationLeftSileRemove : SwitchHook() {
    override val name: String
        get() = "左滑移除会话"
    override val uiItemLocation: String
        get() = FuncRouter.EXPERIMENTAL

    override fun initOnce(): Boolean {
//        "Lcom/tencent/mm/ui/conversation/ConversationListView;".toAppClass().resolve()
//            .firstMethod {
//                name = "onScroll"
//                superclass()
//            }.self.hookAfterIfEnable {
//                if (it.thisObject::class.simpleName != "ConversationListAdapter") return@hookAfterIfEnable
//                val target = it.thisObject.resolve()
//                    .firstMethod {
//                        superclass()
//                        name = "getHeaderViewList"
//                    }.self
//                target.isAccessible = true
//                val result = target.invoke(it.thisObject) as ArrayList<View>
//                it.thisObject.resolve().firstMethod {
//                    name = "removeHeaderView"
//                }.of(it.thisObject).invoke(result[0])
//            }
        "Lcom/tencent/mm/ui/conversation/ConversationFolderItemView;".toAppClass().declaredConstructors.forEach {
            it.hookAfterIfEnable { param ->
                val view = param.thisObject as View
            }
        }
        return true
    }
}