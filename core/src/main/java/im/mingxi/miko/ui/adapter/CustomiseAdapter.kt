package im.mingxi.miko.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import im.mingxi.miko.ui.util.UISetUp

class CustomiseAdapter(private val container: RecyclerView, private val dataSet: List<String>) :
    RecyclerView.Adapter<CustomiseAdapter.CustomViewHolder>() {

    class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView = view.findViewById<TextView>(android.R.id.text1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)
        return CustomViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        with(holder) {
            textView.text = dataSet[position]
            itemView.setOnClickListener {
                container.adapter =
                    MainAdapter(UISetUp.pages[position].wrappers.find { true }!!.items)
            }
        }
    }

    override fun getItemCount(): Int = dataSet.size

}