package im.mingxi.miko.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import im.mingxi.core.databinding.XdialogBinding


class XDialog(val app: Context) : Dialog(app) {
    var title: String = "Miko"
    var isBackButtonEnable: Boolean = true


    val binding: XdialogBinding = XdialogBinding.inflate(
        LayoutInflater.from(app)
    )

    var dialogTitle: TextView
    var dialogRoot: LinearLayout
    var confirmButton: Button
    var dialogContainer: LinearLayout
    var confirmButtonClickListener: View.OnClickListener = View.OnClickListener { v: View ->
        dismiss()
    }


    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        window!!.setBackgroundDrawableResource(android.R.color.transparent)

        this.dialogRoot = binding.getRoot()
        this.confirmButton = binding.xdialogButton
        this.dialogContainer = binding.xdialogContainer
        this.dialogTitle = binding.xdialogTitle

        setContentView(dialogRoot)

        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val width = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            windowManager.currentWindowMetrics.bounds.width()
        } else {
            @Suppress("DEPRECATION") windowManager.defaultDisplay.width
        }

        dialogContainer.layoutParams.width = width / 2 + width / 3
    }

    fun build(): XDialog {
        dialogTitle.text = title
        if (isBackButtonEnable) {
            confirmButton.visibility = View.VISIBLE
            confirmButton.setOnClickListener(confirmButtonClickListener)
        }
        show()
        return this@XDialog
    }

    fun contain(view: View): XDialog {
        dialogContainer.addView(view)
        return this@XDialog
    }

    companion object {
        fun create(app: Context): XDialog {
            return XDialog(app)
        }
    }
}