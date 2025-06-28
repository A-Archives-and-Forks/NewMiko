package im.mingxi.loader.util

import de.robv.android.xposed.XposedBridge
import java.lang.reflect.Field

/*
* 引用自QTool
*/
object FrameUtil {

    fun getFrameName(): String {
        val tag = collectBridgeTag()
        if (tag == "BugHook") return "应用转生"
        if (tag == "LSPosed-Bridge") return "LSPosed"
        if (tag == "SandXposed") return "天鉴"
        if (tag == "PineXposed") return "DreamLand"
        if (tag == "Xposed") {
            try {
                val clz =
                    XposedBridge::class.java.classLoader!!.loadClass("me.weishu.exposed.ExposedBridge")
                if (clz != null) return "太极"
            } catch (_: Exception) {
            }
        }
        return tag
    }

    private fun collectBridgeTag(): String {
        val BUGTag = checkIsBugHook()
            ?: try {
                val f: Field = XposedBridge::class.java.getField("TAG")
                f.isAccessible = true
                return f.get(null) as String
            } catch (e: Exception) {
                return "未知"
            }
        return BUGTag
    }

    private fun checkIsBugHook(): String? {
        val bridgeLoader = XposedBridge::class.java.classLoader
        try {
            val clz = bridgeLoader!!.loadClass("com.bug.hook.xposed.HookBridge")
            val tag: Field = clz.getField("TAG")
            tag.isAccessible = true
            return tag.get(null) as String
        } catch (_: Exception) {
            return null
        }
    }
}