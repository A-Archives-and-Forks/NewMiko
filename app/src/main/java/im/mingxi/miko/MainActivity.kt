package im.mingxi.miko

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.color.DynamicColors
import im.mingxi.miko.databinding.ActivityMainBinding

public class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    
    private val binding: ActivityMainBinding
        get() = checkNotNull(_binding) { "Activity has been destroyed" }
    
    private var lastClickTime = 0L
    private val clickInterval = 1000L
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DynamicColors.applyToActivityIfAvailable(this)
    
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        

        with(window) {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            
            statusBarColor = Color.TRANSPARENT
            navigationBarColor = Color.TRANSPARENT

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                setDecorFitsSystemWindows(false)
            } else {
                @Suppress("DEPRECATION")
                decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                )
            }
        }
    
        setupButtonClickListeners()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
    
    private fun setupButtonClickListeners() {
        // 使用说明
        binding.btnUsage.setOnClickListener {
            if (isFastClick()) return@setOnClickListener
            val intent: Intent = Intent(Intent.ACTION_VIEW)
            intent.setData(Uri.parse("https://miko.afkeru.xyz/"))
            startActivity(intent)
        }

        // 交流讨论
        binding.btnDiscussion.setOnClickListener {
            if (isFastClick()) return@setOnClickListener
        }

        // 更新日志
        binding.btnChangelog.setOnClickListener {
            if (isFastClick()) return@setOnClickListener
        }
    }

    private fun isFastClick(): Boolean {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime < clickInterval) {
            return true
        }
        lastClickTime = currentTime
        return false
    }
    
    fun onModuleLoad() {
        binding.customText.text = "模块激活状态：已激活"
    }
}