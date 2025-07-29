package im.mingxi.miko.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import im.mingxi.core.databinding.SelectItemBinding
import im.mingxi.miko.util.config
import im.mingxi.mm.manager.impl.MMAvatarStorageManagerImpl
import im.mingxi.mm.model.Conservation

class SelectAdapter(val isBlack: Boolean, val dataSet: ArrayList<Conservation>) :
    RecyclerView.Adapter<SelectAdapter.SelectViewHolder>() {

    class SelectViewHolder(val binding: SelectItemBinding) : RecyclerView.ViewHolder(binding.root) {

        val checkbox = binding.check
        val text = binding.title
        val image = binding.image
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SelectViewHolder {
        return SelectViewHolder(SelectItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    private fun getKeyName(): String {
        return if (isBlack) "im.mingxi.mm.hook.AutoRedPacketReceiver.config.blackList" else "im.mingxi.mm.hook.AutoRedPacketReceiver.config.whiteList"
    }

    override fun onBindViewHolder(
        holder: SelectViewHolder,
        position: Int
    ) {
        val item = dataSet[position]
        with(holder) {
            text.text = "${item.nickname}(type: ${item.type})"
            image.setImageBitmap(
                MMAvatarStorageManagerImpl().getAvatarByWxid(
                    item.username,
                    item.nickname
                )
            )
            checkbox.isChecked = config.decodeString(getKeyName(), "")!!.contains(item.username)
            checkbox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    config.encode(
                        getKeyName(),
                        "${config.decodeString(getKeyName(), "")}|${item.username}"
                    )
                } else {
                    config.encode(
                        getKeyName(),
                        config.decodeString(getKeyName(), "")!!.replace("|${item.username}", "")
                    )
                }
            }
        }
    }

    override fun getItemCount(): Int =
        dataSet.size

}