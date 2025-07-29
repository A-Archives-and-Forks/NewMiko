package im.mingxi.mm.hook

import android.annotation.SuppressLint
import android.view.View
import com.highcapable.kavaref.KavaRef.Companion.resolve
import im.mingxi.loader.bridge.XPHelper
import im.mingxi.miko.annotation.FunctionHookEntry
import im.mingxi.miko.hook.SwitchHook
import im.mingxi.miko.startup.util.XRes
import im.mingxi.miko.ui.util.FuncRouter
import im.mingxi.miko.util.toAppClass

@FunctionHookEntry(itemType = FunctionHookEntry.WECHAT_ITEM)
class TranslateToolBar : SwitchHook() {
    override val name: String
        get() = "透明工具栏"
    override val uiItemLocation: String
        get() = FuncRouter.BEAUTIFY

    @SuppressLint("UseCompatLoadingForDrawables", "RestrictedApi")
    override fun initOnce(): Boolean {
        "androidx.appcompat.widget.ActionBarContainer".toAppClass().resolve().firstMethod {
            name = "onDraw"
            superclass()
        }.self
            .hookAfterIfEnable { param ->
                if (XPHelper.getStackData()
                        .contains("com.tencent.mm.ui.conversation.ConversationListView") ||
                    XPHelper.getStackData().contains("androidx.recyclerview.widget.RecyclerView") ||
                    XPHelper.getStackData().contains("com.tencent.mm.ui.mogic.WxViewPager") ||
                    XPHelper.getStackData().contains("androidx.core.widget.NestedScrollView") ||
                    XPHelper.getStackData()
                        .contains("com.tencent.mm.blink.FirstScreenFrameLayout") ||
                    XPHelper.getStackData().contains("com.tencent.mm.ui.FrostedContentView")
                ) return@hookAfterIfEnable

                if (XPHelper.getStackData().contains("android.view.View.draw") &&
                    XPHelper.getStackData()
                        .contains("android.view.View.updateDisplayListIfDirty") &&
                    XPHelper.getStackData().contains("android.view.ViewGroup.drawChild") &&
                    XPHelper.getStackData().contains("android.view.ViewGroup.dispatchDraw") &&
                    XPHelper.getStackData().contains("android.view.ThreadedRenderer.draw")
                ) {

                    val thiz = param.thisObject as View
                    XRes.addAssetsPath(thiz.context)
                    thiz.alpha = 0.9f
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//                        // Kotlin 示例 (在 Activity/Fragment 或 View 相关代码中)
//                        val blurRadius = 25f // 模糊半径，值越大越模糊（建议范围 1-25，具体看效果）
//                        val blurEffect =
//                            RenderEffect.createBlurEffect(
//                                blurRadius, // 水平方向模糊半径
//                                blurRadius, // 垂直方向模糊半径 (通常与水平相同)
//                                Shader.TileMode.CLAMP // 边缘处理模式 (常用 CLAMP 或 DECAL)
//                            )
//


                    //thiz.setRenderEffect(blurEffect)
                    //  }
                    param.unhook()
                }
            }
        return true
    }

}