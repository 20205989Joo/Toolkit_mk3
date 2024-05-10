package com.example.toolkit_management_mk3

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.toolkit_management_mk3.com.example.toolkit_management_mk3.ColorViewModel
import com.example.toolkit_management_mk3.models.Item

class CustomItemAdapter_2(
    private val context: Context,
    private val resource: Int,
    private val identifier: String,
    private var items: MutableList<Item>,
    private val viewModel: ColorViewModel // ViewModel 인스턴스가 전달되어야 함
) : ArrayAdapter<Item>(context, resource, items) {

    // SharedPreferences를 식별자 기반으로 접근
    private val sharedPreferences = context.getSharedPreferences("AppPrefs_$identifier", Context.MODE_PRIVATE)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val itemView = convertView ?: LayoutInflater.from(context).inflate(resource, parent, false)
        val textViewItem = itemView.findViewById<TextView>(R.id.textViewItemMain)
        val subTextView = itemView.findViewById<TextView>(R.id.textViewItemSub)

        val item = getItem(position)
        if (textViewItem == null || subTextView == null) {
            Log.e("CustomItemAdapter_2", "TextViews are null at position $position")
        }

        if (item != null) {
            textViewItem?.text = item.mainText
            subTextView?.text = item.subItems

            val color = getColorForState(viewModel.colorState.value, item.id)
            textViewItem?.setTextColor(color)
            subTextView?.setTextColor(color)

            textViewItem?.setOnClickListener {
                viewModel.updateColorState(context, item.id, identifier) // ViewModel의 메서드를 호출
            }
        } else {
            Log.e("CustomItemAdapter_2", "Item is null at position $position")
            textViewItem?.text = "Invalid item"
            subTextView?.text = "Invalid sub item"
        }

        return itemView
    }

    private fun getColorForState(colorState: Map<Int, Int>?, itemId: Int): Int {
        return when (colorState?.getOrDefault(itemId, 0)) {
            1 -> context.resources.getColor(R.color.upgrade)
            2 -> context.resources.getColor(R.color.red)
            else -> Color.WHITE
        }
    }

    fun updateColorState(newColorState: Map<Int, Int>) {
        notifyDataSetChanged()  // UI 갱신
    }

    fun updateItems(newItems: MutableList<Item>) {
        Log.d("CustomItemAdapter_2", "Before update, current items: ${items.size}, new items: ${newItems.size}")
        if (newItems.isNotEmpty()) {
            this.items.clear()
            this.items.addAll(newItems)
        } else {
            Log.d("CustomItemAdapter_2", "Received empty items list for update.")
        }
        notifyDataSetChanged()
        Log.d("CustomItemAdapter_2", "After update, current items: ${items.size}")
    }
}
