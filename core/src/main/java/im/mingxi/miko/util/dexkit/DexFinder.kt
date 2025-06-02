package im.mingxi.miko.util.dexkit

import android.annotation.SuppressLint
import com.tencent.mmkv.MMKV
import im.mingxi.loader.util.PathUtil
import org.luckypray.dexkit.DexKitBridge
import org.luckypray.dexkit.query.FindClass
import org.luckypray.dexkit.query.FindField
import org.luckypray.dexkit.query.FindMethod

class DexFinder {
    val cache = MMKV.mmkvWithID("global_cache")

    init {
        NativeLoader.loadLibrary("libdexkit.so")
    }

    @Throws(Throwable::class)
    fun DexMethodDescriptor.findDexMethod(findMethod: FindMethod.() -> Unit) {
        val dexKit = DexKitBridge.create(PathUtil.apkPath!!)
        dexKit.use { dexKitBridge ->
            val descriptor = dexKitBridge.findMethod(findMethod).single().descriptor
            cache.encode(this.config, descriptor)
        }
    }

    @SuppressLint("DuplicateCreateDexKit")
    @Throws(Throwable::class)
    fun DexMethodDescriptor.findDexClass(findClass: FindClass.() -> Unit) {
        val dexKit = DexKitBridge.create(PathUtil.apkPath!!)
        dexKit.use { dexKitBridge ->
            val descriptor = dexKitBridge.findClass(findClass).single().descriptor
            cache.encode(this.config, descriptor)
        }
    }

    @SuppressLint("DuplicateCreateDexKit")
    @Throws(Throwable::class)
    fun DexMethodDescriptor.findDexField(findField: FindField.() -> Unit) {
        val dexKit = DexKitBridge.create(PathUtil.apkPath!!)
        dexKit.use { dexKitBridge ->
            val descriptor = dexKitBridge.findField(findField).single().descriptor
            cache.encode(this.config, descriptor)
        }
    }
}