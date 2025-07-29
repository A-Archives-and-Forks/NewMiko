package im.mingxi.mm.bean

import android.content.ContentValues

class RedPacketBody(values: ContentValues) {
    var msgSvrId: Long = values.getAsLong("msgSvrId") ?: 0
    var sender: String? = values.getAsString("sender")
    var sendId: String? = values.getAsString("sendId")
    var mNativeUrl: String? = values.getAsString("mNativeUrl")

    override fun toString(): String {
        return "RedPacketBody(msgSvrId=$msgSvrId, sender=$sender, sendId=$sendId, mNativeUrl=$mNativeUrl)"
    }
}