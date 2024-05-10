package com.example.toolkit_management_mk3

import CheckDialogFragment
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity

class MainActivity_2_Left : AppCompatActivity() {
    private lateinit var mainLayout: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_2_left)

        mainLayout = findViewById(R.id.main_layout_2_left)

        // Override transition as needed
        overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, 0, 0, Color.TRANSPARENT)
        val buttonCheck: Button = findViewById(R.id.buttonCheck_2_left)
        buttonCheck.setOnClickListener {
            val dialog = CheckDialogFragment.newInstance()
            dialog.show(supportFragmentManager, "checkDialog")
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x.toInt()
        val y = event.y.toInt()

        // Define the coordinates for Area E in the left activity
        val gBounds = Rect(92, 1444,660,2300)
        val hBounds = Rect( 772,1500,1396,2328)

        if (event.action == MotionEvent.ACTION_DOWN) {
            when {
                x in gBounds.left..gBounds.right && y in gBounds.top..gBounds.bottom -> {
                    showTouchFeedback(gBounds)
                    DisplayDialogFragment.newInstance("Pen_Tools").show(supportFragmentManager, "displayDialogG")
                }
                x in hBounds.left..hBounds.right && y in hBounds.top..hBounds.bottom -> {
                    showTouchFeedback(hBounds)
                    DisplayDialogFragment.newInstance("Repair_Kit").show(supportFragmentManager, "displayDialogH")
                }
                else -> finish() // 뒤로 가기
            }
            return true
        }
        return super.onTouchEvent(event)
    }

    private fun showTouchFeedback(bounds: Rect) {
        View(this).apply {
            alpha = 0.2f
            setBackgroundColor(Color.WHITE)
            x = bounds.left.toFloat()
            y = bounds.top.toFloat()
            layoutParams = FrameLayout.LayoutParams(bounds.width(), bounds.height())
            mainLayout.addView(this)
            postDelayed({ mainLayout.removeView(this) }, 100)
        }
    }

    override fun finish() {
        super.finish()
        overrideActivityTransition(OVERRIDE_TRANSITION_CLOSE, 0, 0, Color.TRANSPARENT)
    }
}
