package im.mingxi.mm.model

data class DBInsertMsg(
    val msgSvrId: Long,
    val msgSeq: Long,
    val talker: String,
    val content: String,
    val sender: String?
)