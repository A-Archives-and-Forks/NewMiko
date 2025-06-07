package im.mingxi.miko.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tencent.mmkv.MMKV
import im.mingxi.core.R
import im.mingxi.miko.hook.SwitchHook

class MainAdapter(private val dataSet: List<SwitchHook>) :
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
        with(holder) {
            textView.text = currentItem.name
            switch.isChecked = currentItem.isEnabled()
            switch.setOnCheckedChangeListener { _, isChecked ->
                with(currentItem) {
                    if (isChecked) {
                        MMKV.mmkvWithID("global_config").encode(TAG, true)
                        initialize()
                    } else unInitialize()
                }
            }
        }
    }


    override fun getItemCount() = dataSet.size
}