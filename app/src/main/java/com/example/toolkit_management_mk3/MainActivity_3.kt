package com.example.toolkit_management_mk3

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.toolkit_management_mk3.models.Item
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MainActivity_3 : AppCompatActivity() {
    private lateinit var mainLayout: FrameLayout
    private lateinit var fadeTextViewMain: TextView // Main Text를 위한 TextView
    private lateinit var fadeTextViewSub: TextView // Sub Text를 위한 TextView
    private lateinit var buttonEdit: Button

    private var itemList = mutableListOf<Pair<Item, Pair<Int, Int>>>()
    private val colorState = mutableMapOf<Int, Int>() // 색상 상태를 관리하는 맵

    private var lastClickTime = 0L // 마지막 클릭 시간을 저장
    private val doubleClickInterval = 300L // 더블 클릭으로 인식할 시간 간격

    // 색상 상태를 관리하는 함수에서
    private fun getColorForState(itemId: Int): Int {
        return when (colorState.getOrDefault(itemId, 0)) {
            1 -> resources.getColor(R.color.upgrade, theme) // 원하는 색상 리소스
            2 -> resources.getColor(R.color.red, theme) // 원하는 색상 리소스
            else -> Color.WHITE
        }
    }

    // 원형 피드백을 표시하는 함수 (MainActivity_3)
    private fun showStrapCircles() {
        itemList.forEachIndexed { index, (item, coords) ->
            val radius = 90 // 원하는 반지름으로 설정

            // 노란색 원형을 그리기 위해 ShapeDrawable 생성
            val ovalShape = ShapeDrawable(OvalShape()).apply {
                intrinsicWidth = radius * 2
                intrinsicHeight = radius * 2
                paint.color = resources.getColor(R.color.editbutton, theme)
            }

            val feedbackView = View(this).apply {
                alpha = 0.3f
                background = ovalShape // 배경을 원형으로 설정
                this.x = coords.first.toFloat() - radius
                this.y = coords.second.toFloat() - radius
                layoutParams = FrameLayout.LayoutParams(radius * 2, radius * 2)
                tag = "feedbackView" // 피드백 태그 설정
            }

            mainLayout.addView(feedbackView)
            feedbackView.bringToFront()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_3)

        // 활동 전환 애니메이션 설정
        overrideActivityTransition(
            Activity.OVERRIDE_TRANSITION_OPEN,
            R.anim.fade_in,
            R.anim.fade_out,
            Color.TRANSPARENT
        )

        // 레이아웃 초기화
        mainLayout = findViewById(R.id.main_layout_3)
        fadeTextViewMain = findViewById(R.id.textViewFade) // Main Text의 TextView
        fadeTextViewSub = findViewById(R.id.textViewFadeSub) // Sub Text의 TextView
        buttonEdit = findViewById(R.id.buttonEdit)

        fadeTextViewMain.visibility = View.INVISIBLE // 처음에는 보이지 않도록 설정
        fadeTextViewSub.visibility = View.INVISIBLE // SubText도 보이지 않도록 설정

        // 편집 버튼으로 StrapEditActivity 실행
        buttonEdit.setOnClickListener {
            val intent = Intent(this, StrapEditActivity::class.java)
            startActivityForResult(intent, 100)
        }

        // 기존 데이터를 로드
        loadStrapItems()
        loadColorState() // 색상 상태 불러오기

        // 원형 피드백을 항상 표시
        showStrapCircles()
    }

    override fun onResume() {
        super.onResume()
        // 돌아올 때 최신 데이터를 반영하도록 데이터 다시 로드
        loadStrapItems()
        loadColorState() // 최신 색상 상태 로드
        showStrapCircles()
    }

    override fun finish() {
        super.finish()

        // 활동 전환 애니메이션 설정
        overrideActivityTransition(
            Activity.OVERRIDE_TRANSITION_CLOSE,
            R.anim.fade_in,
            R.anim.fade_out,
            Color.TRANSPARENT
        )
    }

    // 터치 이벤트 처리
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x.toInt()
        val y = event.y.toInt()

        if (event.action == MotionEvent.ACTION_DOWN) {
            val currentClickTime = System.currentTimeMillis()
            val itemPair = findItemByCoordinates(x, y)

            if (itemPair != null) {
                val itemId = itemPair.first.id

                if (currentClickTime - lastClickTime < doubleClickInterval) {
                    // 더블 클릭: 색상 상태 변경
                    val currentState = colorState.getOrDefault(itemId, 0)
                    val newState = (currentState + 1) % 3
                    colorState[itemId] = newState
                    saveColorState() // 색상 상태 저장
                }

                lastClickTime = currentClickTime

                // 해당 좌표에 피드백을 표시하고 타이틀을 페이드 처리
                val itemBounds = Rect(itemPair.second.first - 90, itemPair.second.second - 90, itemPair.second.first + 90, itemPair.second.second + 90)
                showTouchFeedback(itemBounds)

                // Main Text의 위치와 색상 설정
                fadeTextViewMain.text = itemPair.first.mainText
                fadeTextViewMain.setTextColor(getColorForState(itemId))
                fadeTextViewMain.x = itemPair.second.first.toFloat() - fadeTextViewMain.width / 2
                fadeTextViewMain.y = itemPair.second.second.toFloat() - fadeTextViewMain.height / 2
                fadeTextViewMain.visibility = View.VISIBLE

                // Sub Text의 위치와 색상 설정
                fadeTextViewSub.text = itemPair.first.subItems
                fadeTextViewSub.setTextColor(Color.LTGRAY)
                fadeTextViewSub.x = fadeTextViewMain.x
                fadeTextViewSub.y = fadeTextViewMain.y + fadeTextViewMain.height
                fadeTextViewSub.visibility = View.VISIBLE

                // 페이드 아웃 애니메이션 설정
                val fadeOut = AlphaAnimation(1.0f, 0.0f).apply {
                    duration = 2000 // 2초 동안 페이드 아웃
                }
                fadeTextViewMain.startAnimation(fadeOut)
                fadeTextViewSub.startAnimation(fadeOut)

                fadeTextViewMain.postDelayed({
                    fadeTextViewMain.visibility = View.INVISIBLE
                    fadeTextViewSub.visibility = View.INVISIBLE
                }, 2000)
            }
            return true
        }
        return super.onTouchEvent(event)
    }

    // 주어진 영역에 하얀색 피드백을 표시하는 함수
    private fun showTouchFeedback(bounds: Rect) {
        val feedbackView = View(this).apply {
            alpha = 0.1f
            setBackgroundColor(Color.WHITE)
            this.x = bounds.left.toFloat()
            this.y = bounds.top.toFloat()
            layoutParams = FrameLayout.LayoutParams(bounds.width(), bounds.height())
        }

        mainLayout.addView(feedbackView)
        feedbackView.bringToFront()

        // 100ms 동안 피드백 표시 후 제거
        feedbackView.postDelayed({ mainLayout.removeView(feedbackView) }, 100)
    }

    // 아이템 리스트를 SharedPreferences에서 로드하는 함수
    private fun loadStrapItems() {
        val sharedPreferences = getSharedPreferences("StrapPrefs", MODE_PRIVATE)
        val jsonItems = sharedPreferences.getString("items", null)
        val jsonFeedback = sharedPreferences.getString("feedbackCoordinates", null)

        if (jsonItems != null && jsonFeedback != null) {
            val items = Gson().fromJson<List<Item>>(jsonItems, object : TypeToken<MutableList<Item>>() {}.type)
            val coordinates = Gson().fromJson<List<Pair<Int, Int>>>(jsonFeedback, object : TypeToken<MutableList<Pair<Int, Int>>>() {}.type)

            // 아이템과 좌표를 결합하여 리스트에 저장
            itemList = items.zip(coordinates).toMutableList()
        }
    }

    // 색상 상태를 SharedPreferences에서 로드하는 함수
    private fun loadColorState() {
        val sharedPreferences = getSharedPreferences("StrapPrefs", MODE_PRIVATE)
        val jsonColorState = sharedPreferences.getString("colorState", "{}")
        val type = object : TypeToken<MutableMap<Int, Int>>() {}.type

        colorState.putAll(Gson().fromJson(jsonColorState, type))
    }

    // 색상 상태를 SharedPreferences에 저장하는 함수
    private fun saveColorState() {
        val sharedPreferences = getSharedPreferences("StrapPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putString("colorState", Gson().toJson(colorState))
        editor.apply()
    }

    // 입력 좌표를 기반으로 아이템을 찾는 함수
    private fun findItemByCoordinates(x: Int, y: Int): Pair<Item, Pair<Int, Int>>? {
        return itemList.firstOrNull { (_, coords) ->
            // 해당 아이템의 좌표가 주어진 범위 내에 있는지 확인
            val itemBounds = Rect(coords.first - 90, coords.second - 90, coords.first + 90, coords.second + 90)
            itemBounds.contains(x, y)
        }
    }
}
