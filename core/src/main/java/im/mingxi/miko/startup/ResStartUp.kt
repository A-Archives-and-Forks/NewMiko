package im.mingxi.miko.startup

import android.content.Context
import im.mingxi.loader.hotpatch.HotPatch.hotPatchAPKPath
import im.mingxi.miko.startup.util.XRes.addAssetsPath

object ResStartUp {
    fun doLoad(ctx: Context) {
        addAssetsPath(ctx, hotPatchAPKPath)
    }
}