package im.mingxi.miko.startup

import android.content.Context
import android.view.LayoutInflater
import im.mingxi.core.BuildConfig
import im.mingxi.loader.bridge.XPBridge
import im.mingxi.loader.hotpatch.HotPatch.hotPatchAPKPath
import im.mingxi.loader.util.PathUtil
import im.mingxi.miko.startup.util.XRes
import im.mingxi.miko.startup.util.XRes.addAssetsPath
import im.mingxi.miko.util.Reflex

object ResStartUp {
    fun doLoad(ctx: Context) {
        val apkPath = if (BuildConfig.DEBUG) PathUtil.moduleApkPath else hotPatchAPKPath
        addAssetsPath(ctx, apkPath)
        XPBridge.hookBefore(
            Reflex.findMethod(LayoutInflater::class.java).setParamsLength(4).get()
        ) {
            XRes.addAssetsPath(it.args[0] as Context, apkPath)
        }
    }
}