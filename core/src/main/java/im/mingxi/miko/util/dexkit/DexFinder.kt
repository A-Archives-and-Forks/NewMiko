package im.mingxi.miko.util.dexkit

import com.tencent.mmkv.MMKV
import im.mingxi.loader.util.PathUtil
import org.luckypray.dexkit.DexKitBridge
import org.luckypray.dexkit.query.FindMethod

class DexFinder {
    val cache = MMKV.mmkvWithID("global_cache")

    init {
        NativeLoader.loadLibrary("libdexkit.so")
    }

    @Throws(Throwable::class)
    fun findDexMethod(dexMethodDescriptor: DexMethodDescriptor, findMethod: FindMethod) {
        val dexKit = DexKitBridge.create(PathUtil.apkPath!!)
        dexKit.use { dexKitBridge ->
            val descriptor = dexKitBridge.findMethod(findMethod).single().descriptor
            cache.encode(dexMethodDescriptor.config, descriptor)
        }
    }
}