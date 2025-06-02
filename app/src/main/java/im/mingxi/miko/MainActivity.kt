package im.mingxi.miko

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.google.android.material.color.DynamicColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import im.mingxi.miko.databinding.ActivityMainBinding

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

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
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setData("https://github.com/hiatus169/Miko-Public?tab=readme-ov-file#-newmiko-".toUri())
            startActivity(intent)
        }

        // 交流讨论
        binding.btnDiscussion.setOnClickListener {
            if (isFastClick()) return@setOnClickListener
            val builder = MaterialAlertDialogBuilder(this)
            val items = arrayOf(
                "Telegram Notification Channel", "Telegram Chat Group", "Github Discussion"
            )
            builder.setTitle("交流讨论")
                .setItems(items) { _, which ->
                    when (which) {
                        0 -> {
                            val intent = Intent(Intent.ACTION_VIEW)
                            intent.setData("https://t.me/wsy666HD".toUri())
                            startActivity(intent)
                        }

                        1 -> {
                            val intent = Intent(Intent.ACTION_VIEW)
                            intent.setData("https://t.me/MikoBuild".toUri())
                            startActivity(intent)
                        }

                        2 -> {
                            val intent = Intent(Intent.ACTION_VIEW)
                            intent.setData("https://github.com/hiatus169/Miko-Public/discussions".toUri())
                            startActivity(intent)
                        }
                    }
                }.show()
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
}