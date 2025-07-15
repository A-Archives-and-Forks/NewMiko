package im.mingxi.miko.ui.widget

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.graphics.toColorInt


class MikoToast private constructor(private val activity: Activity) : Toast(activity) {
    /** 构造所需实例  */
    private val container: LinearLayout = LinearLayout(activity)
    private val textView: TextView = TextView(activity)

    init {

        val drawable = GradientDrawable()
        drawable.setColor("#FFFFFF".toColorInt())
        //  drawable.setAlpha(35);
        drawable.setCornerRadius(25f)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            drawable.setPadding(10, 10, 10, 10)
        }
        drawable.setStroke(1, Color.WHITE)
        drawable.setShape(GradientDrawable.RECTANGLE)

        container.background = drawable
        container.gravity = Gravity.CENTER

        textView.setTextColor("#1A1A1A".toColorInt())
        textView.textSize = 12f
        textView.setPadding(10, 10, 10, 10)

        //  textView.setTypeface(null, Typeface.BOLD);
        container.addView(textView)
        view = container
    }

    companion object {
        fun <T> makeToast(app: Activity, message: T?) {
            app.runOnUiThread(
                Runnable {
                    val toast = MikoToast(app)
                    toast.textView.text = message.toString()
                    toast.show()
                })
        }
    }
}