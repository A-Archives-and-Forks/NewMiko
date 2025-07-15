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
import im.mingxi.miko.ui.activity.SettingActivity
import im.mingxi.miko.util.IntentUtil
import io.github.libxposed.service.XposedService
import io.github.libxposed.service.XposedServiceHelper

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null

    internal val binding: ActivityMainBinding
        get() = checkNotNull(_binding) { "MainActivity has been destroyed" }

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


        XposedServiceHelper.registerListener(object : XposedServiceHelper.OnServiceListener {
            override fun onServiceBind(service: XposedService) {
                binding.customText.text = "状态：已激活"
            }

            override fun onServiceDied(service: XposedService) {
                binding.customText.text = "状态：未激活"
            }
        })
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
            intent.setData("https://github.com/dartcv/Miko-Public?tab=readme-ov-file#-newmiko-".toUri())
            startActivity(intent)
        }

        // 交流讨论
        binding.btnDiscussion.setOnClickListener {
            if (isFastClick()) return@setOnClickListener
            val builder = MaterialAlertDialogBuilder(this)
            val items = arrayOf(
                "Telegram CI Channel", "Telegram Chat Group", "Github Discussion", "QQ Group", "QQ Notification Group"
            )
            builder.setTitle("交流讨论")
                .setItems(items) { _, which ->
                    when (which) {
                        0 -> {
                            val intent = Intent(Intent.ACTION_VIEW)
                            intent.setData("https://t.me/MikoCIBuilds".toUri())
                            startActivity(intent)
                        }

                        1 -> {
                            val intent = Intent(Intent.ACTION_VIEW)
                            intent.setData("https://t.me/MikoChatGroup".toUri())
                            startActivity(intent)
                        }
                        2 -> {
                            val intent = Intent(Intent.ACTION_VIEW)
                            intent.setData("https://github.com/dartcv/Miko-Public/discussions".toUri())
                            startActivity(intent)
                        }
                        3 -> {
                            IntentUtil.openQQGroup(this, "902327702")
                        }
                        4 -> {
                            IntentUtil.openQQGroup(this , "837012640")
                        }

                    }
                }.show()
        }

        // 更新日志
        binding.btnChangelog.setOnClickListener {
            if (isFastClick()) return@setOnClickListener
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
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