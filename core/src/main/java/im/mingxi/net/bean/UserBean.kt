package im.mingxi.net.bean

data class UserBean(
    val loginId: String,
    val loginType: Int,
    val isBan: Boolean,
    val banReason: String,
    val banTime: String
)
