package com.fphoenixcorneae.framework.widget

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ViewConfiguration
import androidx.recyclerview.widget.RecyclerView

/**
 * @desc 解决竖向RecyclerView嵌套横向RecyclerView时, 滑动横向RecyclerView, 竖向RecyclerView抖动的问题
 * @date 2020-09-21 16:04
 */
class RecyclerViewAtRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : RecyclerView(context, attrs, defStyle) {
    private var mScrollPointerId = 0
    private var mInitialTouchX = 0
    private var mInitialTouchY = 0
    private var mTouchSlop = 0

    init {
        val vc = ViewConfiguration.get(context)
        this.mTouchSlop = vc.scaledTouchSlop
    }

    override fun setScrollingTouchSlop(slopConstant: Int) {
        val vc = ViewConfiguration.get(this.context)
        when (slopConstant) {
            TOUCH_SLOP_DEFAULT -> this.mTouchSlop = vc.scaledTouchSlop
            TOUCH_SLOP_PAGING -> this.mTouchSlop = vc.scaledPagingTouchSlop
            else -> Log.w(
                "RecyclerView",
                "setScrollingTouchSlop(): bad argument constant $slopConstant; using default value"
            )
        }
        super.setScrollingTouchSlop(slopConstant)
    }

    override fun onInterceptTouchEvent(e: MotionEvent): Boolean {
        val canScrollHorizontally = layoutManager?.canScrollHorizontally() ?: false
        val canScrollVertically = layoutManager?.canScrollVertically() ?: false
        val action = e.actionMasked
        val actionIndex = e.actionIndex
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                mScrollPointerId = e.getPointerId(0)
                this.mInitialTouchX = (e.x + 0.5f).toInt()
                this.mInitialTouchY = (e.y + 0.5f).toInt()
                return super.onInterceptTouchEvent(e)
            }
            MotionEvent.ACTION_MOVE -> {
                val index = e.findPointerIndex(this.mScrollPointerId)
                if (index < 0) {
                    Log.e(
                        "RecyclerView",
                        "Error processing scroll; pointer index for id " + this.mScrollPointerId + " not found. Did any MotionEvents get skipped?"
                    )
                    return false
                }
                val x = (e.getX(index) + 0.5f).toInt()
                val y = (e.getY(index) + 0.5f).toInt()
                if (scrollState != 1) {
                    val dx = x - this.mInitialTouchX
                    val dy = y - this.mInitialTouchY
                    var startScroll = false
                    if (canScrollHorizontally && Math.abs(dx) > this.mTouchSlop && Math.abs(dx) > Math.abs(
                            dy
                        )
                    ) {
                        startScroll = true
                    }
                    if (canScrollVertically && Math.abs(dy) > this.mTouchSlop && Math.abs(dy) > Math.abs(
                            dx
                        )
                    ) {
                        startScroll = true
                    }
                    Log.d(
                        "RecyclerView",
                        "canX:$canScrollHorizontally--canY$canScrollVertically--dx:$dx--dy:$dy--startScorll:$startScroll--mTouchSlop$mTouchSlop"
                    )
                    return startScroll && super.onInterceptTouchEvent(e)
                }
                return super.onInterceptTouchEvent(e)
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                this.mScrollPointerId = e.getPointerId(actionIndex)
                this.mInitialTouchX = (e.getX(actionIndex) + 0.5f).toInt()
                this.mInitialTouchY = (e.getY(actionIndex) + 0.5f).toInt()
                return super.onInterceptTouchEvent(e)
            }
            else -> {
            }
        }
        return super.onInterceptTouchEvent(e)
    }
}