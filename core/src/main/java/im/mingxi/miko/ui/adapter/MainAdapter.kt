package im.mingxi.miko.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tencent.mmkv.MMKV
import im.mingxi.core.R

class MainAdapter(private val dataSet: List<String>) :
    RecyclerView.Adapter<MainAdapter.MyViewHolder>() {


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.setting_switch_name)
        val switch: Switch = itemView.findViewById(R.id.setting_switch)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.switch_item, parent, false)
        return MyViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentItem = dataSet[position]
        holder.textView.text = currentItem
        holder.switch.setOnCheckedChangeListener { _, isChecked ->
            MMKV.mmkvWithID("global_config").encode(currentItem, isChecked)
        }
    }


    override fun getItemCount() = dataSet.size
}