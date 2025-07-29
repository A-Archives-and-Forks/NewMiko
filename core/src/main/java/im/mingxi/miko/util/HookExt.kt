package im.mingxi.miko.util


import com.tencent.mmkv.MMKV
import im.mingxi.loader.bridge.XPBridge.HookCallback
import im.mingxi.loader.bridge.XPBridge.hookAfter
import im.mingxi.loader.bridge.XPBridge.hookBefore
import im.mingxi.miko.hook.BaseFuncHook
import java.lang.reflect.Member

val config = MMKV.mmkvWithID("global_config")
val cache = MMKV.mmkvWithID("global_cache")


fun BaseFuncHook.hookBeforeIfEnable(member: Member, callback: HookCallback) {
    hookBefore(member) {
        if (!isEnabled()) return@hookBefore
        callback.onInvoke(it)
    }
}

fun BaseFuncHook.hookAfterIfEnable(member: Member, callback: HookCallback) {
    hookAfter(member) {
        if (!isEnabled()) return@hookAfter
        callback.onInvoke(it)
    }
}

fun Member.hookBeforeIfEnable(baseFuncHook: BaseFuncHook, callback: HookCallback) =
    baseFuncHook.hookBeforeIfEnable(this, callback)

fun Member.hookAfterIfEnable(baseFuncHook: BaseFuncHook, callback: HookCallback) =
    baseFuncHook.hookAfterIfEnable(this, callback)