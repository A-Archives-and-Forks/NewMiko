package im.mingxi.miko.ui.util

object FuncRouter {

    const val CHAT = "聊天"
    const val AMUSE = "娱乐"
    const val BEAUTY = "美化"
    const val SETTING_DEBUG = "模块设置及调试"
    const val CONTACT = "联系人"

    val CHAT_MESSAGE_PAGE = arrayOf(CHAT, "消息")
    val CHAT_PIC_VIDEO_PAGE = arrayOf(CHAT, "图片视频")
    val CHAT_VOICE_PAGE = arrayOf(CHAT, "语音")
    val AMUSE_EXPERIENCE_PAGE = arrayOf(AMUSE, "实验性功能")
    val BEAUTY_OTHER_PAGE = arrayOf(BEAUTY, "其他")
    val SETTING_DEBUG_INJECT_PAGE = arrayOf(SETTING_DEBUG, "注入")
    val CONTACT_INFO_PAGE = arrayOf(CONTACT, "信息")

    val mPagesList = arrayOf(
        CHAT,
        CONTACT,
        AMUSE,
        BEAUTY,
        SETTING_DEBUG,
    ).toList()

    fun getWrappersByPage(page: String): List<String> {
        return when (page) {
            CHAT -> listOf(
                "消息",
                "图片视频",
                "语音"
            )

            CONTACT -> listOf(
                "信息"
            )

            AMUSE -> listOf(
                "实验性功能"
            )

            BEAUTY -> listOf(
                "其他"
            )

            SETTING_DEBUG -> listOf(
                "注入"
            )

            else -> listOf()
        }

    }
}