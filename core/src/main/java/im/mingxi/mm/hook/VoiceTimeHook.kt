package im.mingxi.mm.hook

import im.mingxi.miko.annotation.FunctionHookEntry
import im.mingxi.miko.hook.BaseFuncHook
import im.mingxi.miko.util.dexkit.DexFinder
import im.mingxi.miko.util.dexkit.DexMethodDescriptor
import im.mingxi.miko.util.dexkit.IFinder


@FunctionHookEntry(itemName = "劫持语音时长", itemType = FunctionHookEntry.WECHAT_ITEM)
class VoiceTimeHook : BaseFuncHook(defaultEnabled = true), IFinder {
    private val voiceStorageSym = DexMethodDescriptor(this, "${simpleTAG}.Method.voiceStorageSym")

    override fun initOnce(): Boolean {
        voiceStorageSym.toMethod(loader).hookBeforeIfEnable {

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