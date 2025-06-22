package im.mingxi.miko.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.tencent.mmkv.MMKV
import im.mingxi.core.R
import im.mingxi.miko.hook.BaseComponentHook
import im.mingxi.miko.hook.CommonHook
import im.mingxi.miko.hook.SwitchHook

class MainAdapter(private val dataSet: List<BaseComponentHook>) :
    RecyclerView.Adapter<ViewHolder>() {
    private val SWITCH_ITEM = 1
    private val COMMON_ITEM = 2


    class SwitchViewHolder(itemView: View) : ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.setting_switch_name)
        val description: TextView = itemView.findViewById(R.id.setting_switch_description)

        @SuppressLint("UseSwitchCompatOrMaterialCode")
        val switch: Switch = itemView.findViewById(R.id.setting_switch)
    }

    class CommonViewHolder(itemView: View) : ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.setting_item_name)
        val description: TextView = itemView.findViewById(R.id.setting_item_description)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        if (viewType == SWITCH_ITEM) {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.switch_item, parent, false)
            return SwitchViewHolder(itemView)
        }
        throw RuntimeException("Unknown view type")
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val currentItem = dataSet[position]
        if (holder is SwitchViewHolder) {
            with(holder)
            {
                name.text = currentItem.name
                if (currentItem.description != null) {
                    description.text = currentItem.description
                    description.visibility = View.VISIBLE
                }
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
        } else if (holder is CommonViewHolder) {
            with(holder)
            {
                name.text = currentItem.name
                if (currentItem.description != null) description.text = currentItem.description
                if (currentItem is CommonHook && currentItem.onClick != null) {
                    itemView.setOnClickListener(currentItem.onClick!!::invoke)
                }
            }
        }
    }


    override fun getItemCount() = dataSet.size

    override fun getItemViewType(position: Int): Int {
        if (dataSet[position] is SwitchHook) return SWITCH_ITEM
        if (dataSet[position] is CommonHook) return COMMON_ITEM
        return super.getItemViewType(position)
    }
}