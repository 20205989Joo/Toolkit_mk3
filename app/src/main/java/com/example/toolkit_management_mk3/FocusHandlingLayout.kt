package com.example.toolkit_management_mk3

/**import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout

// Define an interface for focus change events
interface FocusChangeCallback {
    fun onFocusLost(view: View)
}

class FocusHandlingLayout : LinearLayout {
    var focusChangeCallback: FocusChangeCallback? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        isFocusable = true
        isClickable = true
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.action == MotionEvent.ACTION_DOWN) {
            val activity = context as? Activity
            val currentFocusedView = activity?.currentFocus
            if (currentFocusedView != null) {
                currentFocusedView.clearFocus()  // Clear focus to trigger focus lost events
                notifyFocusLost(currentFocusedView)
            }
        }
        return super.onInterceptTouchEvent(ev)
    }

    private fun notifyFocusLost(view: View) {
        focusChangeCallback?.onFocusLost(view)
        Log.d("FocusHandlingLayout", "FocusChangeCallback invoked")
    }

}*/