package im.mingxi.mm.hook

import im.mingxi.miko.annotation.FunctionHookEntry
import im.mingxi.miko.hook.SwitchHook
import im.mingxi.miko.ui.util.FuncRouter
import im.mingxi.miko.util.dexkit.DexDesc
import im.mingxi.miko.util.dexkit.IFinder
import org.luckypray.dexkit.DexKitBridge

// 源地址
// https://github.com/HdShare/WAuxiliary_Public/blob/main/app/src/main/kotlin/wx/demo/hook/misc/MockScanHook.kt
// 应捐赠要求添加
@FunctionHookEntry(itemType = FunctionHookEntry.WECHAT_ITEM)
class MockScanHook : SwitchHook(), IFinder {
    override val name: String
        get() = "解除相册扫码限制"
    override val uiItemLocation: String
        get() = FuncRouter.EXPERIMENTAL

    enum class ScanScene(val source: Int, val a8KeyScene: Int) {
        WECHAT_SCAN(0, 4),// 微信扫一扫识别
        ALBUM_SCAN(1, 34),// 手机相册扫码识别
        LONG_PRESS_SCAN(4, 37)// 长按图片识别
    }

    override fun initOnce(): Boolean {
        MethodQBarString.toMethod().hookBeforeIfEnable { param ->
            val source = param.args[2] as Int
            val a8KeyScene = param.args[3] as Int
            val matchedScene =
                ScanScene.entries.find { it.source == source && it.a8KeyScene == a8KeyScene }
            if (matchedScene == ScanScene.ALBUM_SCAN || matchedScene == ScanScene.LONG_PRESS_SCAN) {
                param.args[2] = ScanScene.WECHAT_SCAN.source
                param.args[3] = ScanScene.WECHAT_SCAN.a8KeyScene
            }
        }
        return true
    }

    private val MethodQBarString = DexDesc("$simpleTAG.Method.MethodQBarString")

    override fun dexFind(finder: DexKitBridge) {

        MethodQBarString.findDexMethod(finder) {
            matcher {
                usingEqStrings("MicroMsg.QBarStringHandler", "key_offline_scan_show_tips")
            }

        }
    }
}