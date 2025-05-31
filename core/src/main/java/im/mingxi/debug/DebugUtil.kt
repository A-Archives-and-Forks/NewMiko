package im.mingxi.debug

import im.mingxi.loader.bridge.XPBridge

object DebugUtil {
    fun printAllFieldStatic(clz: Class<*>) {
        clz.declaredFields.forEach { XPBridge.log(it.get(null)) }
    }

}