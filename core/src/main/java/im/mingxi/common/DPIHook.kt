package im.mingxi.common

import android.content.res.Configuration
import android.content.res.Resources
import android.util.DisplayMetrics
import android.view.View
import com.tencent.mmkv.MMKV
import im.mingxi.loader.bridge.XPBridge
import im.mingxi.loader.bridge.XPBridge.HookParam
import im.mingxi.miko.annotation.FunctionHookEntry
import im.mingxi.miko.hook.SwitchHook
import im.mingxi.miko.util.Reflex


@FunctionHookEntry(itemType = FunctionHookEntry.COMMON_ITEM)
class DPIHook : SwitchHook() {
    override val name: String
        get() = "修改dpi"
    override val uiItemLocation: Array<String>
        get() = arrayOf("其他", "娱乐")
    override val description: CharSequence?
        get() = "修改手机分辨率"
    override val onClick: ((View) -> Unit)? = {

    }

    companion object {
        var isLoaded = false
    }

    override fun initOnce(): Boolean {
        if (isLoaded) return true
        isLoaded = true
        val sDPI = MMKV.mmkvWithID("global_config").decodeBool("im.mingxi.common.DPIHook", false)
        if (sDPI) {
            XPBridge.hookBefore(
                Reflex.findMethod(Reflex.loadClass("android.app.ContextImpl"))
                    .setMethodName("setResources")
                    .setParams(Resources::class.java)
                    .get()
            ) { param: HookParam ->
                val resources = param.args[0] as Resources
                val dpi = MMKV.mmkvWithID("global_config").decodeInt("DPI", 0)
                if (dpi != 0) {
                    val displayMetrics = resources.displayMetrics
                    //  XPBridge.log("nowdpi:"+displayMetrics.densityDpi);
                    displayMetrics.densityDpi = dpi
                    displayMetrics.density = (dpi.toFloat()) * 0.00625f
                }
            }
            XPBridge.hookBefore(
                Reflex.findMethod(Resources::class.java)
                    .setMethodName("updateConfiguration")
                    .setParamsLength(3)
                    .get()
            ) { param: HookParam ->
                val dpi = MMKV.mmkvWithID("global_config").decodeInt("DPI", 0)
                if (dpi != 0) {
                    val displayMetrics = param.args[1] as DisplayMetrics
                    if (displayMetrics != null) {
                        displayMetrics.densityDpi = dpi
                        displayMetrics.density = (dpi.toFloat()) * 0.00625f
                        (param.args[0] as Configuration).densityDpi = dpi
                    }
                }
            }
            XPBridge.hookAfter(
                Reflex.findMethod(Resources::class.java).setMethodName("getDisplayMetrics").get()
            ) { param: HookParam ->
                val dpi = MMKV.mmkvWithID("global_config").decodeInt("DPI", 0)
                if (dpi != 0) {
                    val displayMetrics = param.result as DisplayMetrics
                    if (displayMetrics != null) {
                        displayMetrics.densityDpi = dpi
                        displayMetrics.density = (dpi.toFloat()) * 0.00625f
                    }
                }
            }
            XPBridge.hookBefore(
                Reflex.findMethod(Reflex.loadClass("android.content.res.ResourcesImpl"))
                    .setMethodName("updateConfiguration")
                    .setParamsLength(3)
                    .get()
            ) { param: HookParam ->
                val dpi = MMKV.mmkvWithID("global_config").decodeInt("DPI", 0)
                if (dpi != 0) {
                    val displayMetrics = param.args[1] as DisplayMetrics
                    if (displayMetrics != null) {
                        displayMetrics.densityDpi = dpi
                        displayMetrics.density = (dpi.toFloat()) * 0.00625f
                        (param.args[0] as Configuration).densityDpi = dpi
                    }
                }
            }
        }

        return true
    }
}