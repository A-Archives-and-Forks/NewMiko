package im.mingxi.mm.hook

import android.app.Activity
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import androidx.core.widget.doAfterTextChanged
import com.tencent.mmkv.MMKV
import im.mingxi.core.databinding.DpiSettingBinding
import im.mingxi.miko.annotation.FunctionHookEntry
import im.mingxi.miko.hook.SwitchHook
import im.mingxi.miko.ui.dialog.XDialog
import im.mingxi.miko.ui.widget.MikoToast
import im.mingxi.miko.util.Reflex
import im.mingxi.miko.util.dexkit.DexDesc
import im.mingxi.miko.util.dexkit.IFinder
import org.luckypray.dexkit.DexKitBridge


@FunctionHookEntry(itemName = "劫持语音时长", itemType = FunctionHookEntry.WECHAT_ITEM)
class VoiceTimeHook : SwitchHook(), IFinder {
    private val voiceStorageSym = DexDesc("${simpleTAG}.Method.voiceStorageSym")
    override val name: String
        get() = "修改语音时长"
    override val uiItemLocation: String
        get() = "聊天"
    override val onClick: ((View) -> Unit)?
        get() = { v ->
            XDialog.create(v.context).apply {
                title = "修改语音时长(1-60秒)"
                val binding = DpiSettingBinding.inflate(LayoutInflater.from(v.context))
                binding.dpiEdit.setText(
                    MMKV.mmkvWithID("global_config").decodeInt("$TAG/time", 0).toString()
                )
                binding.dpiEdit.doAfterTextChanged { text ->
                    val result = text.toString()
                    if (!TextUtils.isEmpty(result)) mConfig.encode("$TAG/time", result.toInt())
                }

                confirmButtonClickListener = View.OnClickListener {
                    MikoToast.makeToast(app as Activity, "修改成功")
                    dismiss()
                }
                contain(binding.root)
            }.build()
        }

    override fun initOnce(): Boolean {
        voiceStorageSym.toMethod().hookBeforeIfEnable {
            Reflex.findFieldObj(
                it.args[0]
            ).setFieldName(
                "l"
            ).get().set(
                it.args[0],
                MMKV.mmkvWithID("global_config").decodeInt("$TAG/time", 60) * 1000
            )
        }
        return true
    }

    override fun dexFind(finder: DexKitBridge) {

        voiceStorageSym.findDexMethod(finder) {
                matcher {
                    usingStrings("MicroMsg.VoiceStorage", "update failed, no values set")
                }
            }

    }
}