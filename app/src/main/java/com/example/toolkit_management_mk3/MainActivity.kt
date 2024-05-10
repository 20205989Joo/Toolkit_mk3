package com.example.toolkit_management_mk3

import CheckDialogFragment
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var mainLayout: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainLayout = findViewById(R.id.main_layout)

        val buttonCheck: Button = findViewById(R.id.buttonCheck)
        buttonCheck.setOnClickListener {
            val dialog = CheckDialogFragment.newInstance()
            dialog.show(supportFragmentManager, "checkDialog")
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x.toInt()
        val y = event.y.toInt()

        // 새로운 좌표로 Area A와 B, 그리고 M2 정의
        val leftA = 248
        val topA = 860 // 932 - 72
        val rightA = 468
        val bottomA = 1640 // 1712 - 72

        val leftB = 956
        val topB = 852 // 924 - 72
        val rightB = 1188
        val bottomB = 1556 // 1628 - 72

        val leftM2 = 536
        val topM2 = 1748 // 1820 - 72
        val rightM2 = 972
        val bottomM2 = 1892 // 1964 - 72

        val leftC = 652
        val topC = 1972 // 2044 - 72
        val rightC = 1028
        val bottomC = 2264 // 2336 - 72

        val leftD = 508
        val topD = 1412 // 1484 - 72
        val rightD = 956
        val bottomD = 1704 // 1776 - 72

        val hgl1 = Rect(432, 436, 600, 824)
        val hgl2 = Rect(428, 272, 840, 428)
        val hgl3 = Rect(840, 296, 1008, 776)


        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (x in leftA..rightA && y in topA..bottomA) {
                    showTouchFeedback(leftA.toFloat(), topA.toFloat(), rightA - leftA, bottomA - topA)
                    val displayFragmentA = DisplayDialogFragment.newInstance("Strap_Back_:_SLING")
                    displayFragmentA.show(supportFragmentManager, "displayDialogA")
                } else if (x in leftB..rightB && y in topB..bottomB) {
                    showTouchFeedback(leftB.toFloat(), topB.toFloat(), rightB - leftB, bottomB - topB)
                    val displayFragmentB = DisplayDialogFragment.newInstance("Strap_Front_:_QUICK")
                    displayFragmentB.show(supportFragmentManager, "displayDialogB")
                } else if (x in leftC..rightC && y in topC..bottomC) {
                    showTouchFeedback(leftC.toFloat(), topC.toFloat(), rightC - leftC, bottomC - topC)
                    val displayFragmentC = DisplayDialogFragment.newInstance("Mini_EDC")
                    displayFragmentC.show(supportFragmentManager, "displayDialogC")
                } else if (x in leftD..rightD && y in topD..bottomD) {
                    showTouchFeedback(leftD.toFloat(), topD.toFloat(), rightD - leftD, bottomD - topD)
                    val displayFragmentD = DisplayDialogFragment.newInstance("Disposables")
                    displayFragmentD.show(supportFragmentManager, "displayDialogD")
                }else if (x in leftM2..rightM2 && y in topM2..bottomM2) {
                    showTouchFeedback(leftM2.toFloat(), topM2.toFloat(), rightM2 - leftM2, bottomM2 - topM2)
                    val intent = Intent(this, ZoomInActivity::class.java)
                    startActivity(intent)
                } else if (hgl1.contains(x, y) || hgl2.contains(x, y) || hgl3.contains(x, y)) {
                    // HGL 좌표 중 어느 하나를 터치하면 MainActivity_3로 이동
                    showTouchFeedback(
                        hgl2.left.toFloat(),
                        hgl2.top.toFloat(),
                        hgl2.width(),
                        hgl2.height()
                    )
                    val intent = Intent(this, MainActivity_3::class.java)
                    startActivity(intent)
                }
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    private fun showTouchFeedback(x: Float, y: Float, width: Int, height: Int) {
        val feedbackView = View(this).apply {
            alpha = 0.2f
            setBackgroundColor(Color.WHITE)
            this.x = x
            this.y = y
            layoutParams = FrameLayout.LayoutParams(width, height)
        }

        mainLayout.addView(feedbackView)
        feedbackView.postDelayed({ mainLayout.removeView(feedbackView) }, 100)  // 100ms 후에 뷰 제거
    }
}
