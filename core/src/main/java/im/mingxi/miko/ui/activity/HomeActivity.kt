package im.mingxi.miko.ui.activity

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.google.android.material.navigation.NavigationView
import im.mingxi.core.R
import im.mingxi.miko.proxy.BaseActivity
import im.mingxi.miko.ui.util.ProxyActUtil

class HomeActivity : BaseActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var mAppBarConfiguration: AppBarConfiguration
    private lateinit var imageBtn: ImageView
    private lateinit var navController: NavController

    override fun onSupportNavigateUp(): Boolean {
        val navController: NavController =
            Navigation.findNavController(this, R.id.nav_host_fragment_content_main)
        return NavigationUI.navigateUp(
            navController,
            mAppBarConfiguration
        ) || super.onSupportNavigateUp()
    }

    private fun createNavigationView(context: Context): NavigationView {
        val navView = NavigationView(context).apply {
            id = View.generateViewId()
            layoutParams = DrawerLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            ).apply {
                gravity = GravityCompat.START
            }
            fitsSystemWindows = true
            inflateMenu(R.menu.activity_main_drawer)

        }
        drawerLayout.addView(navView)
        return navView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppHomeTheme)
        setContentView(R.layout.activity_home)


        ProxyActUtil.mApp = this

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = createNavigationView(this)
        imageBtn = findViewById(R.id.image_btn)



        mAppBarConfiguration = AppBarConfiguration.Builder(
            R.id.nav_home,
            R.id.nav_function,
            R.id.nav_plugin,
            R.id.nav_more
        ).setOpenableLayout(drawerLayout).build()

        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main)

        //  NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration)
        NavigationUI.setupWithNavController(navView, navController)


        imageBtn.setOnClickListener {
            drawerLayout.openDrawer(navView)
        }
    }


    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        if ((drawerLayout != null) && drawerLayout.isDrawerOpen(navView)) {
            drawerLayout.closeDrawers()
            return
        }
        super.onBackPressed()
    }
}