package im.mingxi.miko.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import im.mingxi.core.R
import im.mingxi.miko.ui.adapter.CustomiseAdapter
import im.mingxi.miko.ui.util.UISetUp


class CustomiseFragment : Fragment() {
    companion object {
        var isInSubPage = false
    }

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
        val adapter = CustomiseAdapter(root, UISetUp.pageNames)
        root.adapter = adapter
        root.layoutManager = LinearLayoutManager(requireContext())

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (isInSubPage) {
                    isInSubPage = false
                    (view as RecyclerView).adapter =
                        CustomiseAdapter(view, UISetUp.pageNames)
                } else {
                    requireActivity().finish()
                }
            }
        }
        // 注册回调到activity的OnBackPressedDispatcher
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }
}