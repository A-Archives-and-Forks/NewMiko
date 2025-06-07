package im.mingxi.mm.hook

import im.mingxi.miko.annotation.FunctionHookEntry
import im.mingxi.miko.hook.SwitchHook
import im.mingxi.miko.util.Reflex
import im.mingxi.miko.util.dexkit.DexFinder
import im.mingxi.miko.util.dexkit.DexMethodDescriptor
import im.mingxi.miko.util.dexkit.IFinder


@FunctionHookEntry(itemName = "劫持语音时长", itemType = FunctionHookEntry.WECHAT_ITEM)
class VoiceTimeHook : SwitchHook(), IFinder {
    private val voiceStorageSym = DexMethodDescriptor(this, "${simpleTAG}.Method.voiceStorageSym")
    override val name: String
        get() = "劫持语音时长"
    override val uiItemLocation: Array<String>
        get() = arrayOf("聊天", "语音")

    override fun initOnce(): Boolean {
        voiceStorageSym.toMethod(loader).hookBeforeIfEnable {
            Reflex.findFieldObj(
                it.args[0]
            ).setFieldName(
                "l"
            ).get().set(
                it.args[0],
                60
            )
        }
        return true
    }

    override fun dexFind(finder: DexFinder) {
        with(finder) {
            voiceStorageSym.findDexMethod {
                matcher {
                    usingStrings("MicroMsg.VoiceStorage", "update failed, no values set")
                }
            }
        }

    }
}