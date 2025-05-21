package im.mingxi.miko.annotation

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class FunctionHookEntry(
    val itemName: String = "NonName",  // 项目内部名称，可不填
    val itemType: Int = COMMON_ITEM// 项目类型，默认为通用项目
) {
    companion object {
        const val COMMON_ITEM: Int = 0
        const val WECHAT_ITEM: Int = 1
        const val QQ_ITEM: Int = 2
        const val TIM_ITEM: Int = 3
        const val COMMON_QQ_TIM_ITEM: Int = 4
    }
}

