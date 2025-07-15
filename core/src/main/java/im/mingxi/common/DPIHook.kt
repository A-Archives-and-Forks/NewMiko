package im.mingxi.common

import android.app.Activity
import android.content.res.Configuration
import android.content.res.Resources
import android.text.TextUtils
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import androidx.core.widget.doAfterTextChanged
import com.tencent.mmkv.MMKV
import im.mingxi.core.databinding.DpiSettingBinding
import im.mingxi.loader.bridge.XPBridge
import im.mingxi.loader.bridge.XPBridge.HookParam
import im.mingxi.miko.annotation.FunctionHookEntry
import im.mingxi.miko.hook.SwitchHook
import im.mingxi.miko.ui.dialog.XDialog
import im.mingxi.miko.ui.util.FuncRouter
import im.mingxi.miko.ui.widget.MikoToast
import im.mingxi.miko.util.Reflex

@FunctionHookEntry(itemType = FunctionHookEntry.COMMON_ITEM)
class DPIHook : SwitchHook() {
    override val name: String
        get() = "修改dpi"
    override val uiItemLocation: String
        get() = FuncRouter.AMUSEMENT
    override val description: CharSequence?
        get() = "危险功能"
    override val onClick: ((View) -> Unit) = { v ->
        XDialog.create(v.context).apply {
            title = "修改dpi"
            val binding = DpiSettingBinding.inflate(LayoutInflater.from(v.context))
            binding.dpiEdit.setText(
                MMKV.mmkvWithID("global_config").decodeInt("DPI", 0).toString()
            )
            binding.dpiEdit.doAfterTextChanged { text ->
                val result = text.toString()
                if (!TextUtils.isEmpty(result)) mConfig.encode("DPI", result.toInt())
            }

            confirmButtonClickListener = View.OnClickListener {
                MikoToast.makeToast(app as Activity, "重启微信生效")
                dismiss()
            }
            contain(binding.root)
        }.build()
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

                    displayMetrics.densityDpi = dpi
                    displayMetrics.density = (dpi.toFloat()) * 0.00625f
                    (param.args[0] as Configuration).densityDpi = dpi

                }
            }
            XPBridge.hookAfter(
                Reflex.findMethod(Resources::class.java).setMethodName("getDisplayMetrics").get()
            ) { param: HookParam ->
                val dpi = MMKV.mmkvWithID("global_config").decodeInt("DPI", 0)
                if (dpi != 0) {
                    val displayMetrics = param.result as DisplayMetrics

                    displayMetrics.densityDpi = dpi
                    displayMetrics.density = (dpi.toFloat()) * 0.00625f

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

                    displayMetrics.densityDpi = dpi
                    displayMetrics.density = (dpi.toFloat()) * 0.00625f
                    (param.args[0] as Configuration).densityDpi = dpi

                }
            }
        }

        return true
    }
}