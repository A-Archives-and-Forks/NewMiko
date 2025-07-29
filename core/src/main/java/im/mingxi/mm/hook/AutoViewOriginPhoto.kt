package im.mingxi.mm.hook

import android.widget.Button
import androidx.core.view.isVisible
import com.highcapable.kavaref.KavaRef.Companion.resolve
import im.mingxi.miko.annotation.FunctionHookEntry
import im.mingxi.miko.hook.SwitchHook
import im.mingxi.miko.ui.util.FuncRouter
import im.mingxi.miko.util.HookEnv
import im.mingxi.miko.util.toAppClass

@FunctionHookEntry(itemType = FunctionHookEntry.WECHAT_ITEM)
class AutoViewOriginPhoto(
    override val name: String = "自动接收原图",
    override val uiItemLocation: String = FuncRouter.CHAT
) : SwitchHook() {
    override fun initOnce(): Boolean {
        "com.tencent.mm.ui.chatting.gallery.ImageGalleryUI".toAppClass()
            .resolve().firstMethod {
                name = "initView"
            }.self.hookAfterIfEnable {
                Thread {
                    Thread.sleep(1500)
                    HookEnv.hostActivity.runOnUiThread {
                        it.thisObject.resolve().field {
                            type = Button::class.java
                        }.forEach {
                            it.get<Button>()?.let { imgBtn ->

                                if (imgBtn.isVisible) {
                                    val keywords = listOf("查看原图", "Full Image")
                                    if (keywords.any { text -> imgBtn.text.contains(text, true) }) {
                                        imgBtn.performClick()
                                    }
                                }

                            }
                        }
                    }
                }.start()
            }
        return true
    }
}