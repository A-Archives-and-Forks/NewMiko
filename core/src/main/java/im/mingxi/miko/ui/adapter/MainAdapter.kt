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
import im.mingxi.miko.util.AppUtil
import im.mingxi.miko.util.HookEnv
import im.mingxi.miko.util.dexkit.IFinder

class MainAdapter(val dataSet: List<BaseComponentHook>) :
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
        } else if (viewType == COMMON_ITEM) {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.common_item, parent, false)
            return CommonViewHolder(itemView)
        }
        throw RuntimeException("Unknown view type")
    }

    override fun onBindViewHolder(holder: ViewHolder, position1: Int) {
        val currentItem = dataSet[holder.adapterPosition]
        if (holder is SwitchViewHolder) {
            with(holder)
            {
                name.text = currentItem.name
                if (currentItem.description != null) {
                    description.text = currentItem.description
                }
                if (currentItem.onClick != null) {
                    itemView.setOnClickListener(currentItem.onClick!!::invoke)
                }
                switch.isChecked = currentItem.isEnabled()
                switch.setOnCheckedChangeListener { _, isChecked ->
                    with(currentItem) {
                        if (isChecked) {
                            MMKV.mmkvWithID("global_config").encode(TAG, true)

                            if (currentItem is IFinder) {
                                if (MMKV.mmkvWithID("global_config").decodeInt(
                                        "${currentItem.TAG}.SIGN",
                                        1
                                    ) == AppUtil.getVersionCode(HookEnv.hostContext)
                                ) {
                                    initialize()
                                } else {
                                    MMKV.mmkvWithID("global_config").encode(
                                        "${currentItem.TAG}.SIGN",
                                        AppUtil.getVersionCode(HookEnv.hostContext)
                                    )
                                    initialize()

                                }
                            } else initialize()

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