package im.mingxi.miko.ui

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager

class LayoutManager(context: Context?) : LinearLayoutManager(context) {
    override fun canScrollVertically(): Boolean = false

    override fun canScrollHorizontally(): Boolean = false
}