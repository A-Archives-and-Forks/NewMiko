package im.mingxi.miko.util

/*
 * 通过String加载宿主/模块 类
 */
fun String.toAppClass(): Class<*>? {
    return Reflex.loadClass(this@toAppClass)
}