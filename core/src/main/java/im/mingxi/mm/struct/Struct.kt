package im.mingxi.mm.struct

import org.luckypray.dexkit.query.FindClass
import org.luckypray.dexkit.query.FindField
import org.luckypray.dexkit.query.FindMethod

open class Struct(
    val name: String,
    val type: Int,
    val findClass: (FindClass.() -> Unit)? = null,
    val findMethod: (FindMethod.() -> Unit)? = null,
    val findField: (FindField.() -> Unit)? = null
) {
    companion object {
        const val TYPE_CLASS = 1
        const val TYPE_MEMBER = 2
        const val TYPE_FIELD = 3
    }

    lateinit var obj: Any // instance
    lateinit var cls: Class<*> // declaredClass
}