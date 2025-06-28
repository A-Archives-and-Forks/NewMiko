package im.mingxi.miko.startup

import android.content.Context
import im.mingxi.loader.hotpatch.HotPatch.hotPatchAPKPath
import im.mingxi.loader.util.Constants
import im.mingxi.loader.util.PathUtil
import im.mingxi.miko.startup.util.XRes.addAssetsPath

object ResStartUp {
    fun doLoad(ctx: Context) {
        val apkPath = if (Constants.isHotPatch) hotPatchAPKPath else PathUtil.moduleApkPath
        addAssetsPath(ctx, apkPath)
    }
}