package im.mingxi.loader.bridge

import android.annotation.SuppressLint
import android.content.Context


object XPHelper {
    @SuppressLint("DiscouragedApi")
    fun getResId(context: Context, resourceName: String?, resourceType: String?): Int {
        return context.resources
            .getIdentifier(resourceType, resourceName, context.packageName)
    }

    fun getStackData(): String {
        val sb = StringBuilder()
        for (stackTraceElement in Thread.currentThread().stackTrace) {
            sb.append(stackTraceElement.toString())
            sb.append("\n")
        }
        return sb.toString()
    }


}