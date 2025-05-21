package im.mingxi.mm.hook

import android.app.Activity
import im.mingxi.loader.bridge.XPBridge
import im.mingxi.miko.annotation.FunctionHookEntry
import im.mingxi.miko.hook.BaseFuncHook
import im.mingxi.miko.util.Reflex

@FunctionHookEntry(itemName = "自动原图/跳过视频大小限制", itemType = FunctionHookEntry.WECHAT_ITEM)
class AutoSendRawPic : BaseFuncHook(defaultEnabled = true) {
    override fun initOnce(): Boolean {
        val albumPreviewUIClass =
            Reflex.loadClass("com.tencent.mm.plugin.gallery.ui.AlbumPreviewUI")
        val targetMet = Reflex.findMethod(albumPreviewUIClass).setMethodName("onCreate").get()
        XPBridge.hookBefore(targetMet) {
            val activity = it.thisObject as Activity
            val intent = activity.intent
            intent.putExtra("GalleryUL_SkipVideoSizeLimit", true);
            intent.putExtra("key_send_raw_image", true);
        }
        return true
    }
}