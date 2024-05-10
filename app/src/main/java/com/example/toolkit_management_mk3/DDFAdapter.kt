package com.example.toolkit_management_mk3.com.example.toolkit_management_mk3

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.toolkit_management_mk3.R
import com.example.toolkit_management_mk3.models.Item

class DDFAdapter(context: Context, items: MutableList<Item>, private val colorState: MutableMap<Int, Int>)
    : ArrayAdapter<Item>(context, 0, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val itemView = convertView ?: LayoutInflater.from(context).inflate(R.layout.custom_dispactview, parent, false)
        val holder = itemView.tag as? ViewHolder ?: ViewHolder(itemView).also { itemView.tag = it }

        val item = getItem(position) ?: return itemView
        holder.mainTextView.text = item.mainText
        holder.subTextView.text = item.subItems

        val color = getColorForState(item.id)
        holder.mainTextView.setTextColor(color)
        holder.subTextView.setTextColor(color)

        return itemView
    }

    private fun getColorForState(itemId: Int): Int {
        return when (colorState.getOrDefault(itemId, 0)) {
            1 -> Color.BLUE
            2 -> Color.RED
            else -> Color.BLACK
        }
    }

    class ViewHolder(view: View) {
        val mainTextView: TextView = view.findViewById(R.id.textViewItemMain)
        val subTextView: TextView = view.findViewById(R.id.textViewItemSub)
    }

    fun updateItems(newItems: List<Item>) {
        clear()
        addAll(newItems)
        notifyDataSetChanged()
    }
}
