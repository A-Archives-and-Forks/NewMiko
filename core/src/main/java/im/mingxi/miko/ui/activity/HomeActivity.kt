package im.mingxi.miko.ui.activity

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import im.mingxi.core.R
import im.mingxi.miko.proxy.BaseActivity
import im.mingxi.miko.startup.HookInstaller
import im.mingxi.miko.ui.adapter.MainAdapter

class HomeActivity : BaseActivity() {
    private var recyclerView: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.activity_home)
        recyclerView = findViewById(R.id.home_recyclerview)

        recyclerView!!.layoutManager = LinearLayoutManager(this)
        recyclerView!!.adapter = MainAdapter(HookInstaller.uiList)

    }
}