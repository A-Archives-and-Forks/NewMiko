package im.mingxi.miko.ui.fragment

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import im.mingxi.core.R
import im.mingxi.loader.XposedPackage
import im.mingxi.loader.bridge.XPBridge
import im.mingxi.miko.ui.LayoutManager
import im.mingxi.miko.ui.adapter.HomeAdapter
import im.mingxi.miko.util.AppUtil
import im.mingxi.miko.util.HookEnv
import im.mingxi.miko.util.Util
import im.mingxi.net.Beans
import im.mingxi.net.bean.ModuleInfo
import org.json.JSONObject
import java.io.File


class HomeFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var loginNameText: TextView
    private lateinit var avatarImage: ImageView
    private lateinit var userInfoContainer: LinearLayout


    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.home_fragment, container, false)
        recyclerView = root.findViewById(R.id.home_recyclerview)
        loginNameText = root.findViewById(R.id.user_info)
        avatarImage = root.findViewById(R.id.avatar_image)
        userInfoContainer = root.findViewById(R.id.home_main_layout)

        recyclerView.layoutManager = LayoutManager(this.context)

        // 获取一些信息
        var abi = Build.SUPPORTED_ABIS.contentToString()
        abi = abi.substring(1, abi.length - 1)
        val moduleInfo = Beans.getBean(ModuleInfo::class.java)
        val intent = requireActivity().intent

        loginNameText.text = "登录账号：${intent.getStringExtra("name")}\n用户状态：白嫖用户"

        if (System.getProperty("Miko.isLogin") == "true") {
            @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS") val userData =
                JSONObject(System.getProperty("Miko.userData"))
            if (userData.optString("isBan", "false") == "true") requireActivity().finishAffinity()
            loginNameText.text = "登录账号：${intent.getStringExtra("name")}\n用户状态：${
                if (userData.optString("Tag") == "null") "赞助用户" else userData.optString("Tag")
            }"

        }

        loginNameText.setOnClickListener {
            Util.setTextClipboard(intent.getStringExtra("wxid")!!)
            Snackbar.make(root, "已复制wxid到剪切板", Snackbar.LENGTH_SHORT).show()
        }

        Glide.with(requireContext())
            .applyDefaultRequestOptions(RequestOptions.bitmapTransform(CircleCrop()))
            .load(File(intent.getStringExtra("avatarPath")!!))
            // .load(MMAvatarStorageManagerImpl().getAvatarByWxid("wxid_xh5txgo29pv522"))
            .into(this.avatarImage)


        recyclerView.adapter = HomeAdapter(
            listOf(
                HomeAdapter.Data(
                    "运行框架",
                    "${XPBridge.getFrameworkName()}-API${XPBridge.getApiLevel()}"
                ),
                HomeAdapter.Data(
                    "宿主状态", "${XposedPackage.packageName}(${
                        AppUtil.getVersionCode(
                            HookEnv.hostContext
                        )
                    })"
                ),
                HomeAdapter.Data(
                    "模块状态",
                    "${moduleInfo.versionName}(${moduleInfo.versionCode})"
                ),
                HomeAdapter.Data("设备SDK", Build.VERSION.SDK_INT.toString()),
                HomeAdapter.Data("设备ABI", abi),

            )
        )


        return root
    }
}