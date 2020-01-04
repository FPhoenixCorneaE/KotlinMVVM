package com.wkz.util.xtoast.listener

import android.view.MotionEvent
import android.view.View
import com.wkz.util.xtoast.XToast

/**
 * View 的触摸事件封装
 */
interface OnTouchListener {
    fun onTouch(toast: XToast?, view: View, event: MotionEvent?): Boolean
}