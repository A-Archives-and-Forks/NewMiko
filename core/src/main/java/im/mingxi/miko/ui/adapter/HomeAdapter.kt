package im.mingxi.miko.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import im.mingxi.core.R


class HomeAdapter(private val dataSet: List<Data>) :
    RecyclerView.Adapter<HomeAdapter.HomeViewHolder>() {

    data class Data(val text: String, val desc: String)
    class HomeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.docs_text)
        val descView: TextView = itemView.findViewById(R.id.docs_desc)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.docs_item, parent, false)
        return HomeViewHolder(view)
    }

    override fun getItemCount(): Int = dataSet.size


    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        with(holder) {
            textView.text = dataSet[position].text
            val desc = dataSet[position].desc
            if ("" == desc) {
                descView.visibility = View.GONE
                textView.setPadding(0, dip2px(textView.context, 15f), 0, 0)
            } else descView.text = desc
            if (position == dataSet.size - 1) {
                itemView.findViewById<View>(R.id.divider).visibility = View.GONE
            }
        }
    }

    private fun dip2px(context: Context, dpValue: Float): Int {
        if (dpValue > 0) {
            val scale = context.resources.displayMetrics.density
            return (dpValue * scale + 0.5f).toInt()
        } else {
            val f = -dpValue
            val scale = context.resources.displayMetrics.density
            return -(f * scale + 0.5f).toInt()
        }
    }
}