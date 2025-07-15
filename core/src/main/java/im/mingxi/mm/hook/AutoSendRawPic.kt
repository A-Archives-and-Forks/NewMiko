package im.mingxi.mm.hook

import android.app.Activity
import im.mingxi.miko.annotation.FunctionHookEntry
import im.mingxi.miko.hook.SwitchHook
import im.mingxi.miko.ui.util.FuncRouter
import im.mingxi.miko.util.Reflex
import im.mingxi.miko.util.hookBeforeIfEnable
import im.mingxi.mm.struct.albumPreviewUIClass

@FunctionHookEntry(itemName = "自动原图/跳过视频大小限制", itemType = FunctionHookEntry.WECHAT_ITEM)
class AutoSendRawPic : SwitchHook() {
    override val name: String
        get() = "自动原图/跳过视频大小限制"
    override val uiItemLocation: String
        get() = FuncRouter.CHAT

    override fun initOnce(): Boolean {
        val targetMet = Reflex.findMethod(albumPreviewUIClass).setMethodName("onCreate").get()
        hookBeforeIfEnable(targetMet) {
            val activity = it.thisObject as Activity
            val intent = activity.intent
            intent.putExtra("GalleryUL_SkipVideoSizeLimit", true);
            intent.putExtra("key_send_raw_image", true);
        }
        return true
    }
}