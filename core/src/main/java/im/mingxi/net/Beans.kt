package im.mingxi.net

object Beans {
    val mBeans = ArrayList<Any>()

    /**
     * Gets a bean which only has one instance.
     * @param cls The class of the bean to get.
     * @return The bean of the class.
     */
    fun <T> getBean(cls: Class<T>): T? {
        synchronized(Beans::class) {
            for (bean in mBeans) {
                if (cls.isInstance(bean)) {
                    return cls.cast(bean)
                }
            }
            throw RuntimeException("Bean not found: " + cls.name)
        }
    }

    fun registerBean(bean: Any): Boolean {
        synchronized(Beans::class) {
            mBeans.add(bean)
            return true
        }
    }


}