package im.mingxi.miko.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import im.mingxi.core.R
import im.mingxi.miko.startup.HookInstaller
import im.mingxi.miko.ui.adapter.CustomiseAdapter
import im.mingxi.miko.ui.adapter.HomeAdapter
import im.mingxi.miko.ui.adapter.MainAdapter
import im.mingxi.miko.ui.util.FuncRouter


class CustomiseFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(
            R.layout.customize_fragment,
            container,
            false
        ) as RecyclerView

        val adapter = CustomiseAdapter(FuncRouter.wrappers())
            //MainAdapter(HookInstaller.uiList)
        root.adapter = adapter
        root.layoutManager = LinearLayoutManager(requireContext())

        return root
    }


}