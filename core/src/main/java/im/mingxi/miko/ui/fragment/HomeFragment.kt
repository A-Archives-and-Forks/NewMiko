package im.mingxi.miko.ui.fragment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import im.mingxi.core.R
import im.mingxi.miko.ui.LayoutManager
import im.mingxi.miko.ui.adapter.HomeAdapter

class HomeFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.home_fragment, container, false)
        recyclerView = root.findViewById(R.id.home_recyclerview)


        recyclerView.layoutManager = LayoutManager(this.context)
        recyclerView.adapter = HomeAdapter(
            listOf(
                HomeAdapter.Data("基本信息", ""),
                HomeAdapter.Data("FrameWork", "LSPosed-API100"),
                HomeAdapter.Data("Host", "com.tencent.mm"),
                HomeAdapter.Data("Module", "0.0.1(1)"),
                HomeAdapter.Data("Android SDK", "35"),
                HomeAdapter.Data("ABI", Build.SUPPORTED_ABIS.contentToString()),

            )
        )


        return root
    }
}