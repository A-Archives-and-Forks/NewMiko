package im.mingxi.miko.ui.util

import android.content.Context

fun Context.dpToPx(dp: Float): Int = (dp * resources.displayMetrics.density).toInt()

fun Context.pxToDp(px: Float): Float = px / resources.displayMetrics.density
