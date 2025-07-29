package im.mingxi.miko.util.dexkit

import com.tencent.mmkv.MMKV
import im.mingxi.loader.util.PathUtil
import org.luckypray.dexkit.DexKitBridge
import org.luckypray.dexkit.query.FindClass
import org.luckypray.dexkit.query.FindField
import org.luckypray.dexkit.query.FindMethod

class DexFinder {
    val cache = MMKV.mmkvWithID("global_cache")
    val dexKit = DexKitBridge.create(PathUtil.apkPath!!)



    @Throws(Throwable::class)
    inline fun DexMethodDescriptor.findDexMethod(findMethod: FindMethod.() -> Unit) {
        val descriptor = dexKit.findMethod(findMethod)[0].descriptor
        //single().descriptor
        cache.encode(this.config, descriptor)
    }

    @Throws(Throwable::class)
    inline fun DexMethodDescriptor.findDexClass(findClass: FindClass.() -> Unit) {
        val descriptor = dexKit.findClass(findClass)[0].descriptor
        cache.encode(this.config, descriptor)
    }

    @Throws(Throwable::class)
    inline fun DexMethodDescriptor.findDexField(findField: FindField.() -> Unit) {
        val descriptor = dexKit.findField(findField)[0].descriptor
        cache.encode(this.config, descriptor)
    }

}