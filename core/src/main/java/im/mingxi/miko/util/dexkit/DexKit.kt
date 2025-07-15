package im.mingxi.miko.util.dexkit

import android.annotation.SuppressLint
import com.tencent.mmkv.MMKV
import im.mingxi.loader.bridge.XPBridge
import im.mingxi.loader.util.PathUtil
import org.luckypray.dexkit.DexKitBridge
import org.luckypray.dexkit.query.FindClass


object DexKit {

    val cache = MMKV.mmkvWithID("global_cache")

    init {
        NativeLoader.loadLibrary("libdexkit.so")
    }

    /*
     * 从缓存中请求一个方法
     */
    fun requireMethodFromCache(cacheName: String): DexMethodDescriptor {
        val mmkv = MMKV.mmkvWithID("global_cache")
        if (mmkv.containsKey(cacheName)) {
            return DexMethodDescriptor(mmkv.decodeString(cacheName)!!)
        } else {
            throw RuntimeException("Do not find method in cache")
        }
    }

    fun requireClassFromCache(cacheName: String): String {
        val mmkv = MMKV.mmkvWithID("global_cache")
        if (mmkv.containsKey(cacheName)) {
            return mmkv.decodeString(cacheName)!!
        } else {
            throw RuntimeException("Do not find class in cache")
        }
    }

    @SuppressLint("DuplicateCreateDexKit")
    @Throws(Throwable::class)
    inline fun findDexClass(name: String, findClass: FindClass.() -> Unit) {

        val dexKit = DexKitBridge.create(PathUtil.apkPath!!)
        dexKit.use { dexKitBridge ->
            val descriptor = dexKitBridge.findClass(findClass).single().name
            XPBridge.log(descriptor)
            cache.encode(name, descriptor)
        }

    }

}