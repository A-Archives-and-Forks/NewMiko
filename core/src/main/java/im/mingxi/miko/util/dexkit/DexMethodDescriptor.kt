package im.mingxi.miko.util.dexkit

import com.tencent.mmkv.MMKV
import im.mingxi.miko.hook.BaseFuncHook
import java.io.Serializable
import java.lang.reflect.Constructor
import java.lang.reflect.Member
import java.lang.reflect.Method
import java.lang.reflect.Modifier


class DexMethodDescriptor : Serializable {
    /**
     * Ljava/lang/Object;
     */
    var declaringClass: String

    /**
     * toString
     */
    var name: String

    /**
     * ()Ljava/lang/String;
     */
    var signature: String

    val config: String?

    var baseFuncHook: BaseFuncHook? = null

    val cache = MMKV.mmkvWithID("global_cache")


    constructor(mBaseFuncHook: BaseFuncHook, mConfig: String) {

        declaringClass = ""
        name = ""
        signature = ""
        config = mConfig
        baseFuncHook = mBaseFuncHook
    }

    constructor(method: Method) {
        declaringClass = getTypeSig(method.declaringClass)
        name = method.name
        signature = getMethodTypeSig(method)
        config = null
    }

    constructor(ctor: Constructor<*>) {
        declaringClass = getTypeSig(ctor.declaringClass)
        name = "<init>"
        signature = getConstructorTypeSig(ctor)
        config = null
    }

    constructor(desc: String) {
        val a = desc.indexOf("->")
        val b = desc.indexOf('(', a)
        require(!(a < 0 || b < 0)) { desc }
        declaringClass = desc.substring(0, a)
        name = desc.substring(a + 2, b)
        signature = desc.substring(b)
        config = null
    }

    constructor(clz: String, n: String, s: String) {
        declaringClass = clz
        name = n
        signature = s
        config = null
    }

    constructor(clz: Class<*>, n: String, s: String) {
        declaringClass = getTypeSig(clz)
        name = n
        signature = s
        config = null
    }

    val declaringClassName: String
        get() = declaringClass.substring(1, declaringClass.length - 1).replace('/', '.')

    override fun toString(): String {
        return "$declaringClass->$name$signature"
    }

    val descriptor: String
        get() = "$declaringClass->$name$signature"

    override fun equals(o: Any?): Boolean {
        if (this === o) {
            return true
        }
        if (o == null || javaClass != o.javaClass) {
            return false
        }
        return toString() == o.toString()
    }

    override fun hashCode(): Int {
        return toString().hashCode()
    }

    @Throws(NoSuchMethodException::class)
    fun toMethod(classLoader: ClassLoader): Method {
        val isInDexSearch = config != null
        if (isInDexSearch) {
            val desc: String = cache.decodeString(config, "")!!
            if (desc == "") {
                if (baseFuncHook is IFinder) {

                    (baseFuncHook as IFinder).dexFind(DexFinder())
                    return toMethod(classLoader)
                }
            }
            val a = desc.indexOf("->")
            val b = desc.indexOf('(', a)
            require(!(a < 0 || b < 0)) { desc }
            declaringClass = desc.substring(0, a)
            name = desc.substring(a + 2, b)
            signature = desc.substring(b)
        }
        try {
            var clz = classLoader.loadClass(
                declaringClass.substring(1, declaringClass.length - 1).replace('/', '.')
            )
            for (m in clz!!.declaredMethods) {
                if (m.name == name && getMethodTypeSig(m) == signature) {
                    m.isAccessible = true
                    return m
                }
            }
            while ((clz!!.superclass.also { clz = it }) != null) {
                for (m in clz!!.declaredMethods) {
                    if (Modifier.isPrivate(m.modifiers) || Modifier
                            .isStatic(m.modifiers)
                    ) {
                        continue
                    }
                    if (m.name == name && getMethodTypeSig(m) == signature) {
                        m.isAccessible = true
                        return m
                    }
                }
            }
            if (isInDexSearch && baseFuncHook is IFinder) {

                (baseFuncHook as IFinder).dexFind(DexFinder())
                return toMethod(classLoader)
            }
            throw NoSuchMethodException("$declaringClass->$name$signature")
        } catch (e: ClassNotFoundException) {
            throw NoSuchMethodException(
                "$declaringClass->$name$signature"
            ).initCause(e) as NoSuchMethodException
        }
    }

    val parameterTypes: List<String>
        get() {
            val params = signature.substring(1, signature.indexOf(')'))
            return splitParameterTypes(params)
        }

    val returnType: String
        get() {
            val index = signature.indexOf(')')
            return signature.substring(index + 1)
        }

    companion object {
        fun getMethodTypeSig(method: Method): String {
            val buf = StringBuilder()
            buf.append("(")
            val types = method.parameterTypes
            for (type in types) {
                buf.append(getTypeSig(type))
            }
            buf.append(")")
            buf.append(getTypeSig(method.returnType))
            return buf.toString()
        }

        fun getConstructorTypeSig(ctor: Constructor<*>): String {
            val buf = StringBuilder()
            buf.append("(")
            val types = ctor.parameterTypes
            for (type in types) {
                buf.append(getTypeSig(type))
            }
            buf.append(")")
            buf.append("V")
            return buf.toString()
        }

        fun getTypeSig(type: Class<*>): String {
            if (type.isPrimitive) {
                if (Integer.TYPE == type) {
                    return "I"
                }
                if (Void.TYPE == type) {
                    return "V"
                }
                if (java.lang.Boolean.TYPE == type) {
                    return "Z"
                }
                if (Character.TYPE == type) {
                    return "C"
                }
                if (java.lang.Byte.TYPE == type) {
                    return "B"
                }
                if (java.lang.Short.TYPE == type) {
                    return "S"
                }
                if (java.lang.Float.TYPE == type) {
                    return "F"
                }
                if (java.lang.Long.TYPE == type) {
                    return "J"
                }
                if (java.lang.Double.TYPE == type) {
                    return "D"
                }
                throw IllegalStateException("Type: " + type.name + " is not a primitive type")
            }
            if (type.isArray) {
                return "[" + getTypeSig(type.componentType)
            }
            return "L" + type.name.replace('.', '/') + ";"
        }

        fun splitParameterTypes(s: String): List<String> {
            var i = 0
            val list = ArrayList<String>()
            while (i < s.length) {
                val c = s[i]
                if (c == 'L') {
                    val j = s.indexOf(';', i)
                    list.add(s.substring(i, j + 1))
                    i = j + 1
                } else if (c == '[') {
                    var j = i
                    while (s[j] == '[') {
                        j++
                    }
                    if (s[j] == 'L') {
                        j = s.indexOf(';', j)
                    }
                    list.add(s.substring(i, j + 1))
                    i = j + 1
                } else {
                    list.add(c.toString())
                }
                i++
            }
            return list
        }

        fun forReflectedMethod(method: Member): DexMethodDescriptor {
            return if (method is Method) {
                DexMethodDescriptor(method)
            } else if (method is Constructor<*>) {
                DexMethodDescriptor(method)
            } else {
                throw IllegalArgumentException("Not a method or constructor: $method")
            }
        }
    }
}