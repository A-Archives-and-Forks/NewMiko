package im.mingxi.miko.startup.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.AssetManager
import android.content.res.Resources
import im.mingxi.loader.util.PathUtil

object XRes {
    @JvmStatic
    fun addAssetsPath(context: Context) {
        addAssetsPath(context.resources, PathUtil.moduleApkPath)
    }

    @JvmStatic
    fun addAssetsPath(context: Context, path: String?) {
        addAssetsPath(context.resources, path)
    }

    @SuppressLint("PrivateApi", "DiscouragedPrivateApi")
    @JvmStatic
    @Throws(Exception::class)
    fun addAssetsPath(resources: Resources, str: String?) {
        val method =
            AssetManager::class.java.getDeclaredMethod("addAssetPath", String::class.java)
        method.isAccessible = true
        method.invoke(resources.assets, str)
    }
}