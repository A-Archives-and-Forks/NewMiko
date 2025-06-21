package im.mingxi.miko.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment
import im.mingxi.core.R
import im.mingxi.miko.ui.util.UISetUp


class CustomiseFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(
            R.layout.customize_fragment,
            container,
            false
        )
        val adapter: ArrayAdapter<String> = ArrayAdapter(
            requireContext(), android.R.layout.simple_list_item_1, UISetUp.pageNames
        )
        (root as ListView).adapter = adapter
        // val adapter = MainAdapter()

        return root
    }
}