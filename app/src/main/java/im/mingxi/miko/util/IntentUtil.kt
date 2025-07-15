package im.mingxi.miko.util

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri


object IntentUtil {

    fun openQQGroup(act: Context, uin: String) {
        val u =
            ("mqq://card/show_pslcard?src_type=internal&version=1&uin="
                    + uin
                    + "&card_type=group&source=qrcode").toUri()
        val `in` = Intent(Intent.ACTION_VIEW, u)
        `in`.setPackage("com.tencent.mobileqq")
        `in`.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        act.startActivity(`in`)
    }

}