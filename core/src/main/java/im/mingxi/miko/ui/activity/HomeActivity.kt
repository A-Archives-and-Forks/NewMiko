package im.mingxi.miko.ui.activity

import android.os.Bundle
import im.mingxi.core.R
import im.mingxi.miko.proxy.BaseActivity

class HomeActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
    }
}