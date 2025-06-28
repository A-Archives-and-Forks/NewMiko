package im.mingxi.miko.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context


object Util {
    fun setTextClipboard(str: String) {
        val manager: ClipboardManager =
            HookEnv.hostContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val data = ClipData.newPlainText("text", str)
        manager.setPrimaryClip(data)
    }
}