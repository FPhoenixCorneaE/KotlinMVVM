package com.wkz.util.xtoast.draggable

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View

/**
 * 移动拖拽处理实现类
 */
class MovingDraggable : BaseDraggable() {
    private var mViewDownX = 0f
    private var mViewDownY = 0f
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mViewDownX = event.x
                mViewDownY = event.y
            }
            MotionEvent.ACTION_MOVE -> {
                val rawMoveX = event.rawX
                val rawMoveY = event.rawY - statusBarHeight
                updateLocation(rawMoveX - mViewDownX, rawMoveY - mViewDownY)
            }
            MotionEvent.ACTION_UP -> return mViewDownX != event.x || mViewDownY != event.y
            else -> {
            }
        }
        return false
    }
}