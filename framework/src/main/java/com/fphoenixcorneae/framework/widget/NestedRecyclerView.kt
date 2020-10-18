package com.fphoenixcorneae.framework.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs

/**
 * @desc 解决竖向RecyclerView嵌套横向RecyclerView所引起的滑动冲突问题
 * @date 2020-10-18 14:48
 */
class NestedRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : RecyclerView(context, attrs, defStyle) {

    private var startX = 0f
    private var startY = 0f
    private var mTouchSlop = 0

    init {
        mTouchSlop = ViewConfiguration.get(context).scaledTouchSlop
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                startY = ev.y
                startX = ev.x
            }
            MotionEvent.ACTION_MOVE -> {
                val endY = ev.y
                val endX = ev.x
                val distanceX = abs(endX - startX)
                val distanceY = abs(endY - startY)
                if (distanceX > mTouchSlop && distanceX > distanceY) {
                    return false
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
            }
        }
        return super.onInterceptTouchEvent(ev)
    }
}