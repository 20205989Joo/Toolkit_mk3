package com.example.toolkit_management_mk3

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.toolkit_management_mk3.models.Item

class StrapColorAdapter(
    context: Context,
    private val resource: Int,
    private val items: MutableList<Item>
) : ArrayAdapter<Item>(context, resource, items) {

    // 색상 상태를 관리하기 위한 맵
    private val colorState = mutableMapOf<Int, Int>()

    // 색상 상태에 따른 색상을 반환
    private fun getColorForState(itemId: Int): Int {
        return when (colorState.getOrDefault(itemId, 0)) {
            1 -> Color.RED // 원하는 색상으로 변경
            2 -> Color.BLUE
            else -> Color.WHITE
        }
    }

    // 색상 상태를 업데이트하고 색상 반영
    fun updateColorStateForItem(itemId: Int): Int {
        val currentState = colorState.getOrDefault(itemId, 0)
        val newState = (currentState + 1) % 3
        colorState[itemId] = newState
        return getColorForState(itemId)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val itemView = convertView ?: LayoutInflater.from(context).inflate(resource, parent, false)
        val item = getItem(position)

        val textViewMain = itemView.findViewById<TextView>(R.id.textViewItemMain)

        if (item != null) {
            textViewMain.text = item.mainText
            textViewMain.setTextColor(getColorForState(item.id))
        } else {
            Log.e("StrapColorAdapter", "Invalid item at position $position")
        }

        return itemView
    }
}
