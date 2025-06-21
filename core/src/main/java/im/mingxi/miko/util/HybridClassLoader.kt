package im.mingxi.miko.util

import android.content.Context
import androidx.annotation.Keep


@Keep
class HybridClassLoader private constructor() : ClassLoader(sBootClassLoader) {
    // we shall use findClass() instead of loadClass(), because we did not have a parent ClassLoader
    @Throws(ClassNotFoundException::class)
    override fun findClass(name: String): Class<*> {
        try {
            return sBootClassLoader!!.loadClass(name)
        } catch (ignored: ClassNotFoundException) {
        }
        if (sLoaderParentClassLoader != null && name.startsWith("im.mingxi.")) {
            return sLoaderParentClassLoader!!.loadClass(name)
        }
        if (isConflictingClass(name)) {
            // Nevertheless, this will not interfere with the host application,
            // classes in host application SHOULD find with their own ClassLoader, eg Class.forName()
            // use shipped androidx and kotlin lib.
            throw ClassNotFoundException(name)
        }
        // The ClassLoader for some apk-modifying frameworks are terrible,
        // XposedBridge.class.getClassLoader()
        // is the sane as Context.getClassLoader(), which mess up with 3rd lib, can cause the ART to
        // crash.
        if (sLoaderParentClassLoader != null) {
            try {
                return sLoaderParentClassLoader!!.loadClass(name)
            } catch (ignored: ClassNotFoundException) {
            }
        }
        if (hostClassLoader != null) {
            try {
                return hostClassLoader!!.loadClass(name)
            } catch (ignored: ClassNotFoundException) {
            }
        }
        throw ClassNotFoundException(name)
    }

    companion object {
        private val sBootClassLoader: ClassLoader? = Context::class.java.classLoader

        val INSTANCE: HybridClassLoader = HybridClassLoader()

        private var sLoaderParentClassLoader: ClassLoader? = null
        var hostClassLoader: ClassLoader? = null

        fun setLoaderParentClassLoader(loaderClassLoader: ClassLoader) {
            sLoaderParentClassLoader =
                if (loaderClassLoader === HybridClassLoader::class.java.classLoader) {
                    null
                } else {
                    loaderClassLoader
                }
        }


        /**
         * @param name NonNull, class name
         * @return true if conflicting
         */
        fun isConflictingClass(name: String): Boolean {
            return name.startsWith("androidx.")
                    || name.startsWith("android.support.")
                    || name.startsWith("kotlin.")
                    || name.startsWith("kotlinx.")
                    || name.startsWith("com.tencent.mmkv.")
                    || name.startsWith("com.google.android.")
                    || name.startsWith("org.intellij.lang.annotations.")
                    || name.startsWith("org.jetbrains.annotations.")
                    || name.startsWith("com.bumptech.glide.")
                    || name.startsWith("javax.annotation.")
                    || name.startsWith("_COROUTINE.")
        }
    }
}