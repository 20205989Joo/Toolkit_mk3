package com.example.toolkit_management_mk3

import android.content.Context
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import com.example.toolkit_management_mk3.models.Item
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// ArrayAdapter를 상속받는 CustomItemAdapter_1 클래스를 정의합니다.
class CustomItemAdapter_1(
    private val context: Context, // 컨텍스트를 전달받습니다.
    private val resource: Int, // 사용할 레이아웃 리소스 ID를 전달받습니다.
    private val identifier: String,  // 식별자를 생성자로부터 받음
    var items: MutableList<Item> // 데이터로 사용될 Item 객체 리스트를 전달받습니다.
) : ArrayAdapter<Item>(context, resource, items) {

    // SharedPreferences에 접근하기 위한 프로퍼티입니다.
    private val sharedPreferences = context.getSharedPreferences("AppPrefs_$identifier", Context.MODE_PRIVATE)


    // 각 아이템의 뷰를 생성하고 설정하는 메소드입니다.
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // convertView를 재사용하거나 새로 생성합니다.
        val view = convertView ?: LayoutInflater.from(context).inflate(resource, parent, false)

        // EditText와 Button을 뷰에서 찾습니다.
        val editTextItem: EditText = view.findViewById(R.id.editMainText) // 메인 텍스트를 입력하는 EditText를 찾습니다.
        val editTextSubItem: EditText = view.findViewById(R.id.editSubItem) // 서브 텍스트를 입력하는 EditText를 찾습니다.
        val buttonDelete: Button = view.findViewById(R.id.buttonDelete) // 삭제 버튼을 찾습니다.

        // 해당 position의 아이템을 가져옵니다.
        val item = items[position]
        editTextItem.text = Editable.Factory.getInstance().newEditable(item.mainText) // 메인 텍스트를 설정합니다.
        editTextSubItem.text = Editable.Factory.getInstance().newEditable(item.subItems) // 서브 텍스트를 설정합니다.

        // EditText 클릭 시 포커스 요청
        editTextItem.setOnClickListener {
            editTextItem.requestFocus()
        }
        editTextSubItem.setOnClickListener {
            editTextSubItem.requestFocus()
        }

        editTextItem.onFocusChangeListener = null
        editTextSubItem.onFocusChangeListener = null

        // 메인 EditText의 포커스가 변경될 때 이벤트를 처리합니다.
        editTextItem.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) { // 포커스를 잃었을 때
                saveData(position, editTextItem.text.toString(), items[position].subItems) // 데이터 저장 메서드 호출
            }
        }

        // 서브 EditText의 포커스가 변경될 때 이벤트를 처리합니다.
        editTextSubItem.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) { // 포커스를 잃었을 때
                saveData(position, items[position].mainText, editTextSubItem.text.toString()) // 데이터 저장 메서드 호출
            }
        }

        //notifyDataSetChanged()

        // 삭제 버튼 클릭 이벤트를 처리합니다.
        buttonDelete.setOnClickListener {
            items.removeAt(position) // 아이템 리스트에서 해당 아이템 삭제
            notifyDataSetChanged() // 변경사항을 알려 UI를 갱신
            saveItemsAsync() // 비동기적으로 아이템 저장
        }

        return view // 완성된 뷰 반환
    }

    // 새로운 아이템 리스트로 데이터를 업데이트합니다.
    fun updateItems(newItems: MutableList<Item>) {
            items.clear() // 현재 리스트를 비웁니다.
            items.addAll(newItems) // 새로운 아이템 리스트를 추가합니다.
            notifyDataSetChanged() // 변경사항을 알려 UI를 갱신합니다.
            saveItemsAsync() // UI 갱신 후에 데이터를 저장합니다.
    }

    // 데이터 저장 함수
    private fun saveData(position: Int, mainText: String, subText: String) {
        val item = items[position] // 현재 아이템을 리스트에서 가져옴

        // 메인 텍스트나 서브 텍스트에 변동이 있을 경우만 데이터를 저장
        if (item.mainText != mainText || item.subItems != subText) {
            item.mainText = mainText // 아이템의 메인 텍스트를 업데이트
            item.subItems = subText // 아이템의 서브 텍스트를 업데이트
            notifyDataSetChanged() // 데이터 변경을 알림. 얘를 빼서 hasfocus 부분으로 옮기자.
            saveItemsAsync() // 비동기적으로 sharedpreference에 아이템을 저장
            Log.d("CustomItemAdapter_1", "Data saved at position $position: MainText = '$mainText', SubText = '$subText'") // 로그에 저장 성공 메시지 기록
        } else { // 변경사항이 없을 때의 로직
            Log.d("CustomItemAdapter_1", "No changes detected at position $position") // 변경사항이 없으면 로그에 기록
        }
    }


    // 비동기적으로 아이템 목록을 저장하는 함수
    private fun saveItemsAsync() {
        CoroutineScope(Dispatchers.IO).launch { // IO 스레드에서 코루틴 실행
            val json = Gson().toJson(items) // 아이템 리스트를 JSON 형태로 변환
            sharedPreferences.edit().putString("items", json).apply() // JSON 문자열을 SharedPreferences에 저장
        }
    }

}
