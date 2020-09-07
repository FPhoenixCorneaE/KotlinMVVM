package com.fphoenixcorneae.util.xtoast.draggable

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
        // 获取当前触摸点在 屏幕 的位置
        val rawMoveX = event.rawX
        val rawMoveY = event.rawY - statusBarHeight
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // 获取当前触摸点在 View 的位置
                mViewDownX = event.x
                mViewDownY = event.y
                mWhetherMove = false
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                // 更新移动的位置
                mWhetherMove = true
                updateLocation(rawMoveX - mViewDownX, rawMoveY - mViewDownY)
            }
            MotionEvent.ACTION_UP -> {
                if (!mWhetherMove) {
                    onClickListener?.onClick(xToast, v)
                }
                return true
            }
            else -> {
            }
        }
        return false
    }
}