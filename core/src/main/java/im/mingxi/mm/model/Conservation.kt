package im.mingxi.mm.model

data class Conservation(
    val username: String,
    val nickname: String,
    val type: Int,
    val conRemark: String,
    val quanPin: String,
    val pyInitial: String,
    val conRemarkPYFull: String,
    val conRemarkPYShort: String
)