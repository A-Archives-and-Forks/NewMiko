package im.mingxi.mm.struct

import im.mingxi.miko.util.Reflex.findMethod
import im.mingxi.miko.util.Reflex.loadClass

object MMPreferenceAdapter : Struct() {
    override val hostClass: Class<*> =
        findMethod(loadClass("com.tencent.mm.ui.base.preference.MMPreference"))
            .setMethodName("createAdapter")
            .get()
            .returnType
}