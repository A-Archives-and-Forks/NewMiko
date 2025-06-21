package im.mingxi.miko.hook

import android.view.View

abstract class CommonHook(defaultEnabled: Boolean = false) : BaseComponentHook(defaultEnabled) {
    var onClick: ((v: View) -> Unit)? = null
}