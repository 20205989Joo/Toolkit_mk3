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

class MainActivity_2 : AppCompatActivity() {
    private lateinit var mainLayout: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_2)

        mainLayout = findViewById(R.id.main_layout_2)
        overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, 0, 0, Color.TRANSPARENT)

        val buttonCheck: Button = findViewById(R.id.buttonCheck_2)
        buttonCheck.setOnClickListener {
            val dialog = CheckDialogFragment.newInstance()
            dialog.show(supportFragmentManager, "checkDialog")
        }
    }

    override fun finish() {
        super.finish()
        overrideActivityTransition(OVERRIDE_TRANSITION_CLOSE, 0, 0, Color.TRANSPARENT)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x.toInt()
        val y = event.y.toInt()

        // Define coordinates for Areas M2L, M2R, E, and F
        val m2lBounds = Rect(16, 1560, 192, 2476)
        val m2rBounds = Rect(1252, 1588, 1428, 2468)
        val eBounds = Rect(272, 1860, 412, 2312)
        val fBounds = Rect(1060, 1860, 1188, 2292)
// 새 영역 정의
        val kBounds = Rect(404, 1700, 692, 1920)
        val lBounds = Rect(764, 1986, 996, 2296)
        val mBounds = Rect(356, 1224, 1084, 1556)
        val nBounds = Rect(356, 888, 1084, 1176)

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                when {
                    x in m2lBounds.left..m2lBounds.right && y in m2lBounds.top..m2lBounds.bottom -> {
                        showTouchFeedback(m2lBounds)
                        val intent = Intent(this, MainActivity_2_Left::class.java)
                        startActivity(intent)
                    }

                    x in m2rBounds.left..m2rBounds.right && y in m2rBounds.top..m2rBounds.bottom -> {
                        showTouchFeedback(m2rBounds)
                        val intent = Intent(this, MainActivity_2_Right::class.java)
                        startActivity(intent)
                    }

                    x in eBounds.left..eBounds.right && y in eBounds.top..eBounds.bottom -> {
                        showTouchFeedback(eBounds)
                        DisplayDialogFragment.newInstance("First_Aid")
                            .show(supportFragmentManager, "displayDialogE")
                    }

                    x in fBounds.left..fBounds.right && y in fBounds.top..fBounds.bottom -> {
                        showTouchFeedback(fBounds)
                        DisplayDialogFragment.newInstance("Toiletry")
                            .show(supportFragmentManager, "displayDialogF")
                    }

                    x in kBounds.left..kBounds.right && y in kBounds.top..kBounds.bottom -> {
                        showTouchFeedback(kBounds)
                        DisplayDialogFragment.newInstance("Cutlery_&_Snacks")
                            .show(supportFragmentManager, "displayDialogK")
                    }

                    x in lBounds.left..lBounds.right && y in lBounds.top..lBounds.bottom -> {
                        showTouchFeedback(lBounds)
                        DisplayDialogFragment.newInstance("Stations")
                            .show(supportFragmentManager, "displayDialogL")
                    }

                    x in mBounds.left..mBounds.right && y in mBounds.top..mBounds.bottom -> {
                        showTouchFeedback(mBounds)
                        DisplayDialogFragment.newInstance("Emergency")
                            .show(supportFragmentManager, "displayDialogM")
                    }

                    x in nBounds.left..nBounds.right && y in nBounds.top..nBounds.bottom -> {
                        showTouchFeedback(nBounds)
                        DisplayDialogFragment.newInstance("Ladies_First")
                            .show(supportFragmentManager, "displayDialogN")
                    }
                }
                return true
            }
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
}