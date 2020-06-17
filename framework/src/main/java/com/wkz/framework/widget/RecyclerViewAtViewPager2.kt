package com.wkz.framework.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs

/**
 * @desc：viewPager2嵌套recyclerView滑动冲突，本来重写ViewPager2或RecyclerViewImpl的
 *        onInterceptTouchEvent方法是最好的，但是ViewPager2是个final类，无法重写，
 *        而RecyclerViewImpl是ViewPager2的私有类，也无法被继承，所以要解决滑动冲突，就只能重写RecyclerView了
 * @date：2020-06-17 14:25
 */
class RecyclerViewAtViewPager2 : RecyclerView {
    constructor(context: Context) : super(context) {}
    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : super(context, attrs) {
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
    }

    private var startX = 0
    private var startY = 0
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = ev.x.toInt()
                startY = ev.y.toInt()
                parent.requestDisallowInterceptTouchEvent(true)
            }
            MotionEvent.ACTION_MOVE -> {
                val endX = ev.x.toInt()
                val endY = ev.y.toInt()
                val disX = abs(endX - startX)
                val disY = abs(endY - startY)
                when {
                    disX > disY -> {
                        parent.requestDisallowInterceptTouchEvent(canScrollHorizontally(startX - endX))
                    }
                    else -> {
                        parent.requestDisallowInterceptTouchEvent(canScrollVertically(startY - endY))
                    }
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                parent.requestDisallowInterceptTouchEvent(false)
            }
            else -> {
            }
        }
        return super.dispatchTouchEvent(ev)
    }
}