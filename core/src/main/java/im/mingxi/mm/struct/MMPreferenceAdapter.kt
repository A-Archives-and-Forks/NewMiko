package im.mingxi.mm.struct

import im.mingxi.miko.util.Reflex
import im.mingxi.miko.util.Reflex.loadClass

class MMPreferenceAdapter : Struct() {
    override val hostClass: Class<*> =
        Reflex.findMethod(loadClass("com.tencent.mm.ui.base.preference.MMPreference"))
            .setMethodName("createAdapter")
            .get()
            .returnType
}