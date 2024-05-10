package com.example.toolkit_management_mk3

// 필요한 안드로이드 및 코틀린 라이브러리를 임포트합니다.
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.toolkit_management_mk3.models.Item
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

// AppCompatActivity를 상속받아 메인 액티비티 클래스를 정의합니다.
class PocketActivity : AppCompatActivity() {
    // UI 요소와 변수를 선언합니다.
    private lateinit var listView: ListView // 아이템을 표시할 ListView
    private lateinit var adapter: CustomItemAdapter_1 // ListView의 어댑터
    private var pocketList = mutableListOf<Item>() // 아이템을 저장할 리스트
    private lateinit var addTextMainItem: EditText // 메인 텍스트 입력을 위한 EditText
    private lateinit var addTextSubItem: EditText // 서브 텍스트 입력을 위한 EditText
    private lateinit var buttonAddItem: Button // 아이템 추가 버튼
    private var lastItemId = 0 // 마지막 아이템 ID를 추적하는 변수
    private lateinit var identifier: String

    // 액티비티 생성 시 호출되는 메소드
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.item_adding_layout) // 액티비티의 레이아웃을 설정
        // 인텐트에서 식별자를 받아옴
        identifier = intent.getStringExtra("IDENTIFIER") ?: "Default"


        initializeViews() // UI 컴포넌트를 초기화
        setupListeners() // UI 리스너를 설정
        loadData() // 데이터를 로드

    }

    // ID를 통해 뷰를 찾아 초기화하는 메소드
    private fun initializeViews() {
        listView = findViewById(R.id.listViewItems) // ListView 연결, 찾기
        addTextMainItem = findViewById(R.id.AddMainText) // 메인 EditText 연결, 찾기
        addTextSubItem = findViewById(R.id.AddSubItem) // 서브 EditText 연결, 찾기
        buttonAddItem = findViewById(R.id.buttonAddItem) // 추가 버튼 연결
        // PocketActivity에서 CustomItemAdapter_1 초기화
        adapter = CustomItemAdapter_1(this, R.layout.item_edittext_layout, identifier, pocketList)
        listView.adapter = adapter // ListView에 어댑터 연결
        listView.setTouchAndFocusProperties() // 터치 및 포커스 속성 설정
        findViewById<LinearLayout>(R.id.main_layout).setTouchAndFocusProperties() // 메인 레이아웃에 터치와 포커스 속성 설정
    }

    // 뷰의 터치 및 포커스 속성을 설정하는 메소드
    private fun View.setTouchAndFocusProperties() {
        isFocusable = true // 뷰를 포커스 가능하게 설정
        isClickable = true // 뷰를 클릭 가능하게 설정
        isFocusableInTouchMode = true // 터치 모드에서 포커스 가능하게 설정
    }

    // 리스너를 설정하는 메소드
    private fun setupListeners() {
        buttonAddItem.setOnClickListener { manageItemAddition() } // 추가 버튼에 리스너 설정
        setupTouchListener() // 터치 리스너 설정
    }
    // 터치 이벤트를 처리하는 리스너를 설정합니다.
      private fun setupTouchListener() {
        val commonTouchListener = View.OnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // 기존 로직 유지
                    clearFocusAndHideKeyboard()
                    false
                }
                MotionEvent.ACTION_UP -> {
                    v.performClick() // 클릭 이벤트 수동 호출
                    false
                }
                else -> false
            }
        }
        findViewById<LinearLayout>(R.id.main_layout).apply {
            setOnTouchListener(commonTouchListener) // 메인 레이아웃에 터치 리스너 설정
            isFocusable = true // 포커스 가능하게 설정
            isClickable = true // 클릭 가능하게 설정
            isFocusableInTouchMode = true // 터치 모드에서 포커스 가능하게 설정
        }

        listView.apply {
            setOnTouchListener(commonTouchListener) // 리스트뷰에도 동일한 터치 리스너 적용
            setTouchAndFocusProperties() // 터치 및 포커스 속성 설정
        }
    }

    // 포커스를 클리어하고 키보드를 숨기는 함수
    private fun clearFocusAndHideKeyboard() {
        currentFocus?.let {
            it.clearFocus() // 현재 포커스 있는 뷰에서 포커스 제거
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0) // 키보드 숨김
        }
    }

    // 아이템 추가 관리
    private fun manageItemAddition() {
        loadData() // 데이터 로드
        val mainItemText = addTextMainItem.text.toString() // 메인 텍스트 입력 필드에서 텍스트 가져오기
        val subItemText = addTextSubItem.text.toString() // 서브 텍스트 입력 필드에서 텍스트 가져오기

        if (mainItemText.isNotBlank()) { // 메인 텍스트가 비어있지 않다면
            val newItem = Item(++lastItemId, mainItemText, subItemText) // 새 아이템 생성
            pocketList.add(newItem) // 포켓리스트에 추가
            adapter.updateItems(pocketList) // customitemadapter에 포켓리스트 전달하고, 리스트 비우고, 리스트 갱신. 그리고 UI갱신.
            addTextMainItem.text.clear() // 메인 텍스트 입력 필드 클리어
            addTextSubItem.text.clear() // 서브 텍스트 입력 필드 클리어
            saveItems() // json에 아이템 저장
            Log.d("PocketActivity", "Added new item with ID: ${newItem.id}") // 새 아이템의 ID 출력
        } else {
            Log.d("PocketActivity", "Main text is blank") // 로그 출력
        }
    }

    // 데이터 로드 함수
    private fun loadData() {
        val sharedPreferences = getSharedPreferences("AppPrefs_$identifier", Context.MODE_PRIVATE) // Shared Preferences 접근
        lastItemId = sharedPreferences.getInt("lastItemId_$identifier", 0) // lastItemId 로드
        val json = sharedPreferences.getString("items", null) // 아이템 데이터를 JSON 형태로 가져오기
        if (json != null) { // JSON 데이터가 있다면
            pocketList = Gson().fromJson(json, object : TypeToken<MutableList<Item>>() {}.type) // JSON을 아이템 리스트로 변환
            Log.d("PocketActivity", "Loaded items: ${pocketList.size}") // 로그 출력
        } else { // JSON 데이터가 없다면
            Log.d("PocketActivity", "No items found in SharedPreferences") // 로그 출력
            pocketList.clear() // 아이템 리스트 클리어. 없는상태.
        }
        adapter.updateItems(pocketList) // 어댑터에 아이템 리스트 업데이트
    }

    // 아이템 저장 함수
    private fun saveItems() {
        val json = Gson().toJson(pocketList) // 아이템 리스트를 JSON으로 변환
        val sharedPreferences = getSharedPreferences("AppPrefs_$identifier", Context.MODE_PRIVATE) // Shared Preferences 접근
        with (sharedPreferences.edit()) { // Shared Preferences 에디터를 가져옴
            putString("items_$identifier", json) // JSON 형태의 아이템 데이터를 저장
            putInt("lastItemId_$identifier", lastItemId) // lastItemId 저장
            apply() // 변경사항을 적용
        }
    }
}
//리스너 설정 없이도 listview가 아니라 main layout을 터치하면 포커스와 커서는 일단 없어짐. 키보드는 안없어짐.