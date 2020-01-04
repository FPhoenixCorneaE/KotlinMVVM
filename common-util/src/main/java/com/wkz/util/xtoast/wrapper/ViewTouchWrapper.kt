package com.wkz.util.xtoast.wrapper

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import com.wkz.util.xtoast.XToast
import com.wkz.util.xtoast.listener.OnTouchListener

/**
 * [View.OnTouchListener] 包装类
 */
class ViewTouchWrapper(
    private val mToast: XToast,
    view: View,
    private val mListener: OnTouchListener
) : View.OnTouchListener {
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        return mListener.onTouch(mToast, v, event)
    }

    init {
        view.isFocusable = true
        view.isEnabled = true
        view.isClickable = true
        view.setOnTouchListener(this)
    }
}