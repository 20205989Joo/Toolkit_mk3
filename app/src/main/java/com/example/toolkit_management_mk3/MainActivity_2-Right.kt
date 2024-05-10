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

class MainActivity_2_Right : AppCompatActivity() {
    private lateinit var mainLayout: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_2_right)

        mainLayout = findViewById(R.id.main_layout_2_right)

        // Override transition as needed
        overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, 0, 0, Color.TRANSPARENT)
        val buttonCheck: Button = findViewById(R.id.buttonCheck_2_right)
        buttonCheck.setOnClickListener {
            val dialog = CheckDialogFragment.newInstance()
            dialog.show(supportFragmentManager, "checkDialog")
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x.toInt()
        val y = event.y.toInt()

        // Define the coordinates for Area E in the left activity
        val iBounds = Rect(24,1568,692,2340)
        val jBounds = Rect(768,1600,1372,2328)

        if (event.action == MotionEvent.ACTION_DOWN) {
            when {
                x in iBounds.left..iBounds.right && y in iBounds.top..iBounds.bottom -> {
                    showTouchFeedback(iBounds)
                    DisplayDialogFragment.newInstance("Main_Power_Supply").show(supportFragmentManager, "displayDialogI")
                }
                x in jBounds.left..jBounds.right && y in jBounds.top..jBounds.bottom -> {
                    showTouchFeedback(jBounds)
                    DisplayDialogFragment.newInstance("Tech_Acc.s").show(supportFragmentManager, "displayDialogJ")
                }
                else -> finish()
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
