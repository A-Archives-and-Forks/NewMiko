package im.mingxi.miko.util

import com.tencent.mmkv.MMKV
import im.mingxi.loader.bridge.XPBridge.HookCallback
import im.mingxi.loader.bridge.XPBridge.hookAfter
import im.mingxi.loader.bridge.XPBridge.hookBefore
import im.mingxi.miko.hook.BaseFuncHook
import java.lang.reflect.Member

val config = MMKV.mmkvWithID("global_config")

fun BaseFuncHook.hookBeforeIfEnable(member: Member, callback: HookCallback) {
    hookBefore(member) {
        if (!config.decodeBool(this.javaClass.name, false)) return@hookBefore
        callback.onInvoke(it)
    }
}

fun BaseFuncHook.hookAfterIfEnable(member: Member, callback: HookCallback) {
    hookAfter(member) {
        if (!config.decodeBool(this.javaClass.name, false)) return@hookAfter
        callback.onInvoke(it)
    }
}
