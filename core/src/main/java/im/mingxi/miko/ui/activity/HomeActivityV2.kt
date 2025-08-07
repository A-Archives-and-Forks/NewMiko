package im.mingxi.miko.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import im.mingxi.core.R
import im.mingxi.miko.hook.BaseComponentHook
import im.mingxi.miko.proxy.BaseActivity
import im.mingxi.miko.ui.adapter.MainAdapter
import im.mingxi.miko.ui.util.FuncRouter
import im.mingxi.miko.ui.util.ProxyActUtil
import im.mingxi.miko.util.Util
import org.json.JSONObject
import java.io.File

class HomeActivityV2 : BaseActivity(), View.OnClickListener {
    private val avatarImage by lazy { findViewById<ImageView>(R.id.avatar_image_v2) }
    private val recyclerView by lazy { findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recyclerview_main) }
    private val searchView by lazy { findViewById<EditText>(R.id.search_home) }
    private val userInfo by lazy { findViewById<TextView>(R.id.userinfo_v2) }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppHomeTheme)
        setContentView(R.layout.activity_home)

        ProxyActUtil.mApp = this
        requestHideNavigationBar()

        userInfo.setOnClickListener { v ->
            Util.setTextClipboard(intent.getStringExtra("wxid")!!)
            Snackbar.make(v, "已复制wxid到剪切板", Snackbar.LENGTH_SHORT).show()
        }

        userInfo.text =
            "${intent.getStringExtra("name")}    白嫖用户\n${intent.getStringExtra("wxid")!!}"

        if (System.getProperty("Miko.isLogin") == "true") {
            @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS") val userData =
                JSONObject(System.getProperty("Miko.userData"))
            if (userData.optString("isBan", "false") == "true") finishAffinity()
            userInfo.text = "${intent.getStringExtra("name")}    ${
                if (userData.optString("Tag") == "null") "赞助用户" else userData.optString("Tag")
            }\n${intent.getStringExtra("wxid")!!}"
            searchView.hint = if (userData.optString(
                    "Say",
                    "null"
                ) == "null"
            ) "暂无个性签名" else userData.optString("Say")
        }

        searchView.setCompoundDrawablesWithIntrinsicBounds(
            ContextCompat.getDrawable(this, R.drawable.search_24px),
            null,
            null, // 右侧图标初始为 null
            null
        )

        searchView.doAfterTextChanged { s ->

            if (!TextUtils.isEmpty(searchView.text)) {
                val items = FuncRouter.mItems()
                val list = ArrayList<BaseComponentHook>()
                for (item in items) {
                    if (item.name.contains(searchView.text.toString())) list.add(item)
                }
                recyclerView.adapter = MainAdapter(list)
            }
            if (TextUtils.isEmpty(s)) recyclerView.adapter = MainAdapter(FuncRouter.mItems())

            val clearIcon = if (s.isNullOrEmpty()) null
            else ContextCompat.getDrawable(this, R.drawable.close_24px)

            searchView.setCompoundDrawablesWithIntrinsicBounds(
                ContextCompat.getDrawable(this, R.drawable.search_24px),
                null,
                clearIcon,
                null
            )

        }

        searchView.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawables = searchView.compoundDrawables
                val clearDrawable = drawables[2] // 右侧drawable

                if (clearDrawable != null) {
                    // 计算点击位置是否在清除图标区域内
                    val touchX = event.x
                    val clearIconStart =
                        searchView.width - searchView.paddingEnd - clearDrawable.intrinsicWidth

                    if (touchX > clearIconStart) {
                        searchView.text.clear()
                        true
                    }
                }
            }
            false
        }



        Glide.with(this)
            .applyDefaultRequestOptions(RequestOptions.bitmapTransform(CircleCrop()))
            .load(File(intent.getStringExtra("avatarPath")!!))
            .into(this.avatarImage)

        val adapter = MainAdapter(FuncRouter.mItems())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    override fun onClick(v: View) {
        when (v.id) {

        }
    }
}