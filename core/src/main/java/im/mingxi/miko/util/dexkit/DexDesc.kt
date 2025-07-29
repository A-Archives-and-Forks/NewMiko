package im.mingxi.miko.util.dexkit

import android.annotation.SuppressLint
import im.mingxi.miko.util.HookEnv
import im.mingxi.miko.util.cache
import org.luckypray.dexkit.DexKitBridge
import org.luckypray.dexkit.query.FindClass
import org.luckypray.dexkit.query.FindField
import org.luckypray.dexkit.query.FindMethod
import org.luckypray.dexkit.wrap.DexClass
import org.luckypray.dexkit.wrap.DexField
import org.luckypray.dexkit.wrap.DexMethod
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Method

@SuppressLint("NonUniqueDexKitData")
open class DexDesc(val descName: String) {
    private val mLoader = HookEnv.hostClassLoader

    inline fun findDexClass(dexKit: DexKitBridge, findClass: FindClass.() -> Unit) {
        val desc = dexKit.findClass(findClass).firstOrNull()
        if (desc != null) cache.encode(this@DexDesc.descName, desc.descriptor)
    }

    inline fun findDexMethod(dexKit: DexKitBridge, findMethod: FindMethod.() -> Unit) {
        val desc = dexKit.findMethod(findMethod).firstOrNull()
        if (desc != null) cache.encode(this@DexDesc.descName, desc.descriptor)
    }

    inline fun findDexField(dexKit: DexKitBridge, findField: FindField.() -> Unit) {
        val desc = dexKit.findField(findField).firstOrNull()
        if (desc != null) cache.encode(this@DexDesc.descName, desc.descriptor)
    }

    fun toDexClass(): DexClass = DexClass(cache.decodeString(descName)!!)
    fun toDexMethod(): DexMethod = DexMethod(cache.decodeString(descName)!!)
    fun toDexField(): DexField = DexField(cache.decodeString(descName)!!)

    fun toClass(): Class<*> = toDexClass().getInstance(mLoader)
    fun toMethod(): Method = toDexMethod().getMethodInstance(mLoader).apply { isAccessible = true }
    fun toConstructor(): Constructor<*> =
        toDexMethod().getConstructorInstance(mLoader).apply { isAccessible = true }

    fun toField(): Field = toDexField().getFieldInstance(mLoader).apply { isAccessible = true }
}