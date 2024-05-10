package com.example.toolkit_management_mk3

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import com.example.toolkit_management_mk3.models.Item

class StrapAdapter(
    private val context: Context,
    private val items: MutableList<Item>,
    private val onDelete: (Int) -> Unit // 삭제 콜백 함수 추가
) : BaseAdapter() {

    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): Any {
        return items[position]
    }

    override fun getItemId(position: Int): Long {
        return items[position].id.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val viewHolder: ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_strap, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        // 현재 항목에 해당하는 데이터를 가져옴
        val item = items[position]

        // 번호와 메인 타이틀 설정
        viewHolder.textViewTitleNumber.text = "${position + 1} : ${item.mainText}"

        // 서브 타이틀 설정
        viewHolder.textViewSubTitle.text = item.subItems

        // 삭제 버튼 동작 정의
        viewHolder.buttonDelete.setOnClickListener {
            onDelete(position) // 콜백 함수 호출
        }

        return view
    }

    // ViewHolder 클래스에 서브 타이틀 추가
    private class ViewHolder(view: View) {
        val textViewTitleNumber: TextView = view.findViewById(R.id.textViewTitleNumber)
        val textViewSubTitle: TextView = view.findViewById(R.id.textViewSubTitle)
        val buttonDelete: Button = view.findViewById(R.id.buttonDeleteItem)
    }
}
