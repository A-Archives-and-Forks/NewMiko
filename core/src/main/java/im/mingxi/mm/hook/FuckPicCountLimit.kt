package im.mingxi.mm.hook

import android.app.Activity
import im.mingxi.miko.annotation.FunctionHookEntry
import im.mingxi.miko.hook.SwitchHook
import im.mingxi.miko.ui.util.FuncRouter
import im.mingxi.miko.util.Reflex
import im.mingxi.miko.util.hookBeforeIfEnable

@FunctionHookEntry(/*项目内部名称，可选*/itemName = "去你妈的图片数量限制",/*作用宿主，默认均可作用*/
    itemType = FunctionHookEntry.WECHAT_ITEM
)
class FuckPicCountLimit :
    SwitchHook()/*目前你继承im.mingxi.miko.hook下的哪个都没啥用，没写界面，只能默认打开*/ {
    override val name: String
        get() = "去除发送图片数量限制"
    override val uiItemLocation: String
        get() = FuncRouter.CHAT

    override fun initOnce(): Boolean {
        // 通过Reflex类获取Class对象
        val albumPreviewUIClass =
            Reflex.loadClass("com.tencent.mm.plugin.gallery.ui.AlbumPreviewUI")
        // 获取方法
        val targetMet = Reflex.findMethod(albumPreviewUIClass).setMethodName("onCreate").get()
        hookBeforeIfEnable(targetMet) {
            //实现逻辑
            val activity = it.thisObject as Activity
            val intent = activity.intent
            intent.putExtra("max_select_count", 6666)
        }
        return true // 返回true表示初始化成功
    }
}