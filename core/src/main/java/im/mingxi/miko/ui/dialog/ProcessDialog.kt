package im.mingxi.miko.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import im.mingxi.core.R
import im.mingxi.miko.util.HookEnv


class ProcessDialog(val appContext: Context, val message: String) : Dialog(appContext) {

    /* private var _binding: DialogWaitBinding? = null

     private val binding: DialogWaitBinding
         get() = checkNotNull(_binding) { "Activity has been destroyed" }
 */
    lateinit var textView: TextView


    constructor(message: String) : this(HookEnv.hostActivity, message)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        //this._binding = DialogWaitBinding.inflate(layoutInflater)
        //setContentView(binding.root)
        setContentView(R.layout.dialog_wait)

        // Set feature of ProcessDialog
        setCanceledOnTouchOutside(false)
        setCancelable(false)


        //this.textView = binding.waitTv
        this.textView = findViewById(R.id.wait_tv)

        this.textView.text = message


        window?.setBackgroundDrawableResource(android.R.color.transparent)
        val width =
            (appContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.width
        val layoutParams = window!!.attributes
        layoutParams.width = width / 2 + width / 3
        window!!.attributes = layoutParams

    }
}