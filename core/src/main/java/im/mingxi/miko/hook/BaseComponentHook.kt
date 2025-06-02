package im.mingxi.miko.hook

abstract class BaseComponentHook : BaseFuncHook() {
    /**
     * Name of the function.
     */
    abstract val name: String

    /**
     * Description of the function.
     */
    open val description: CharSequence? = null

    abstract val uiItemLocation: Array<String>
}