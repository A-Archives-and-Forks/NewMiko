package im.mingxi.miko.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import im.mingxi.core.R
import im.mingxi.miko.ui.LayoutManager
import im.mingxi.miko.ui.util.FuncRouter


class CustomiseAdapter(private val dataSet: List<String>) :
    RecyclerView.Adapter<CustomiseAdapter.CustomViewHolder>() {

    class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView = view.findViewById<TextView>(R.id.wrapper_text)
        val recyclerView = view.findViewById<RecyclerView>(R.id.wrapper_content)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.setting_wrapper, parent, false)
        return CustomViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        with(holder) {
            textView.text = dataSet[position]
            recyclerView.adapter = MainAdapter(FuncRouter.items(dataSet[position]))
            recyclerView.layoutManager = LayoutManager(recyclerView.context)
            itemView.setOnClickListener {
                if (recyclerView.visibility == View.VISIBLE) recyclerView.visibility = View.GONE
                else recyclerView.visibility = View.VISIBLE
            }
        }
    }

    override fun getItemCount(): Int = dataSet.size

}