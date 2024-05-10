package com.example.toolkit_management_mk3

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.toolkit_management_mk3.models.Item
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class StrapEditActivity : AppCompatActivity() {
    private var x: Int = -1
    private var y: Int = -1
    private lateinit var editTextX: EditText
    private lateinit var editTextY: EditText
    private lateinit var editTextTitle: EditText
    private lateinit var editTextSubtitle: EditText
    private lateinit var buttonSave: Button
    private lateinit var listViewItems: ListView
    private lateinit var mainLayout: FrameLayout
    private lateinit var strapAdapter: StrapAdapter

    private var lastItemId = 0 // 마지막 아이템 ID를 추적하는 변수
    private var itemList = mutableListOf<Item>() // 아이템을 저장할 리스트
    private var feedbackCoordinates = mutableListOf<Pair<Int, Int>>() // 좌표를 저장할 리스트

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_strap_edit)


        // 활동 전환 애니메이션 설정
        overrideActivityTransition(
            Activity.OVERRIDE_TRANSITION_OPEN,
            0,
            0,
            Color.TRANSPARENT
        )

        // 레이아웃 초기화
        mainLayout = findViewById(R.id.main_layout)
        editTextX = findViewById(R.id.strapX)
        editTextY = findViewById(R.id.strapY)
        editTextTitle = findViewById(R.id.strapTitle)
        editTextSubtitle = findViewById(R.id.strapSubtitle)
        buttonSave = findViewById(R.id.buttonSave)
        listViewItems = findViewById(R.id.listViewItems)

        // SharedPreferences에서 기존 데이터 로드
        loadData()

        // 어댑터 초기화 및 ListView에 연결
        strapAdapter = StrapAdapter(this, itemList) { position ->
            // 삭제 콜백 함수 실행
            itemList.removeAt(position)
            feedbackCoordinates.removeAt(position)
            saveItems()
            strapAdapter.notifyDataSetChanged()
            updateFeedbackCircles()
        }
        listViewItems.adapter = strapAdapter

        // 터치 이벤트를 통해 좌표를 설정
        mainLayout.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                x = event.x.toInt()
                y = event.y.toInt()

                editTextX.setText(x.toString())
                editTextY.setText(y.toString())
            }
            false
        }

        // 좌표 및 텍스트를 저장
        buttonSave.setOnClickListener {
            val mainText = editTextTitle.text.toString()
            val subText = editTextSubtitle.text.toString()
            val item = Item(
                id = ++lastItemId,
                mainText = mainText,
                subItems = subText,
                color = Color.WHITE
            )

            itemList.add(item)
            feedbackCoordinates.add(Pair(x, y)) // 좌표 추가
            saveItems()
            strapAdapter.notifyDataSetChanged()

            // 텍스트 필드 초기화
            editTextX.text.clear()
            editTextY.text.clear()
            editTextTitle.text.clear()
            editTextSubtitle.text.clear()

            // 피드백 UI 업데이트
            updateFeedbackCircles()
        }

        // 기존 피드백을 액티비티 로딩 시 생성
        updateFeedbackCircles()
    }

    // SharedPreferences에서 기존 데이터를 로드하는 함수
    private fun loadData() {
        val sharedPreferences = getSharedPreferences("StrapPrefs", Context.MODE_PRIVATE)
        lastItemId = sharedPreferences.getInt("lastItemId", 0)
        val jsonItems = sharedPreferences.getString("items", null)
        val jsonFeedback = sharedPreferences.getString("feedbackCoordinates", null)

        if (jsonItems != null) {
            itemList = Gson().fromJson(jsonItems, object : TypeToken<MutableList<Item>>() {}.type)
        }

        if (jsonFeedback != null) {
            feedbackCoordinates = Gson().fromJson(jsonFeedback, object : TypeToken<MutableList<Pair<Int, Int>>>() {}.type)
        }
    }

    // SharedPreferences에 아이템 및 피드백 데이터를 저장하는 함수
    private fun saveItems() {
        val jsonItems = Gson().toJson(itemList)
        val jsonFeedback = Gson().toJson(feedbackCoordinates)
        val sharedPreferences = getSharedPreferences("StrapPrefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("items", jsonItems)
            putString("feedbackCoordinates", jsonFeedback)
            putInt("lastItemId", lastItemId)
            apply()
        }
    }

    override fun finish() {
        super.finish()

        // 활동 전환 애니메이션 설정
        overrideActivityTransition(
            Activity.OVERRIDE_TRANSITION_CLOSE,
            0,
            0,
            Color.TRANSPARENT
        )
    }

    // 원형 피드백을 표시하는 함수 (항목 번호 및 타이틀 포함)
    private fun showFeedbackCircle(centerX: Int, centerY: Int, radius: Int, position: Int) {
        val feedbackTag = "feedbackView"

        // 원형 영역을 표시하는 뷰
        val feedbackView = View(this).apply {
            alpha = 0.5f
            setBackgroundColor(Color.WHITE)
            this.x = centerX.toFloat() - radius
            this.y = centerY.toFloat() - radius
            layoutParams = FrameLayout.LayoutParams(radius * 2, radius * 2)
            tag = feedbackTag // 피드백 태그 설정
        }

        // 항목 번호의 텍스트 크기를 원형 지름의 80%로 계산
        val numberTextSize = radius * 0.6f

        // 항목 번호를 표시하는 텍스트 뷰
        val textViewNumber = TextView(this).apply {
            text = "$position"
            textSize = numberTextSize
            setTextColor(Color.argb(150, 0, 0, 0)) // 반투명 검정
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            )
            tag = feedbackTag // 피드백 태그 설정
        }

        // 항목 타이틀을 가져와 24sp 크기로 표시하는 텍스트 뷰
        val item = itemList.getOrNull(position - 1)
        val titleTextSize = 24f

        val textViewTitle = TextView(this).apply {
            text = item?.mainText ?: ""
            textSize = titleTextSize
            setTextColor(Color.WHITE) // 흰색 글씨
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            )
            tag = feedbackTag // 피드백 태그 설정
        }

        // 부 타이틀에 대한 설정
        val subtitleTextSize = 16f
        val textViewSubtitle = TextView(this).apply {
            text = item?.subItems ?: ""
            textSize = subtitleTextSize
            setTextColor(Color.WHITE) // 밝은 회색 글씨
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            )
            tag = feedbackTag // 피드백 태그 설정
        }


        // 텍스트 뷰의 중심이 주어진 좌표에 오도록 배치
        textViewNumber.post {
            textViewNumber.x = centerX.toFloat() - textViewNumber.width / 2
            textViewNumber.y = centerY.toFloat() - textViewNumber.height / 2
        }

        textViewTitle.post {
            textViewTitle.x = centerX.toFloat() - textViewTitle.width / 2
            textViewTitle.y = centerY.toFloat() - textViewTitle.height / 2
        }

        textViewSubtitle.post {
            textViewSubtitle.x = textViewTitle.x
            textViewSubtitle.y = textViewTitle.y + textViewTitle.height
        }

        // 피드백 뷰와 텍스트 뷰 추가
        mainLayout.addView(feedbackView)
        mainLayout.addView(textViewNumber)
        mainLayout.addView(textViewTitle)
        mainLayout.addView(textViewSubtitle)


        feedbackView.bringToFront()
        textViewNumber.bringToFront()
        textViewTitle.bringToFront()
        textViewSubtitle.bringToFront()
    }





    // 피드백 UI를 업데이트하는 함수
    private fun updateFeedbackCircles() {
        // 피드백 관련 뷰만 지움 (특정 태그를 가진 뷰만 삭제)
        val feedbackTag = "feedbackView"
        val childCount = mainLayout.childCount
        for (i in childCount - 1 downTo 0) {
            val child = mainLayout.getChildAt(i)
            if (child.tag == feedbackTag) {
                mainLayout.removeViewAt(i)
            }
        }

        // 각 좌표에 대해 원형 피드백을 그림
        feedbackCoordinates.forEachIndexed { index, pair ->
            showFeedbackCircle(pair.first, pair.second, 90, index + 1)
        }
    }
}
