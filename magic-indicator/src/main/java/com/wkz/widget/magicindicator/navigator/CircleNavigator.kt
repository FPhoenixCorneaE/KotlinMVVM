package com.wkz.widget.magicindicator.navigator

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator

import com.wkz.widget.magicindicator.IPagerNavigator
import com.wkz.widget.magicindicator.UIUtil

import java.util.ArrayList

/**
 * 圆圈式的指示器
 */
class CircleNavigator(context: Context) : View(context), IPagerNavigator {
    private var mRadius: Int = 0
    var circleColor: Int = 0
        set(circleColor) {
            field = circleColor
            invalidate()
        }
    private var mStrokeWidth: Int = 0
    private var mCircleSpacing: Int = 0
    private var mCurrentIndex: Int = 0
    // 此处不调用invalidate，让外部调用notifyDataSetChanged
    var circleCount: Int = 0
    var startInterpolator: Interpolator? = LinearInterpolator()
        set(startInterpolator) {
            field = startInterpolator
            if (this.startInterpolator == null) {
                field = LinearInterpolator()
            }
        }

    private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mCirclePoints = ArrayList<PointF>()
    private var mIndicatorX: Float = 0.toFloat()

    /**
     * 事件回调
     */
    var isTouchable: Boolean = false
    var circleClickListener: OnCircleClickListener? = null
        set(circleClickListener) {
            if (!isTouchable) {
                isTouchable = true
            }
            field = circleClickListener
        }
    private var mDownX: Float = 0.toFloat()
    private var mDownY: Float = 0.toFloat()
    private var mTouchSlop: Int = 0

    /**
     * 是否跟随手指滑动
     */
    var isFollowTouch = true

    var radius: Int
        get() = mRadius
        set(radius) {
            mRadius = radius
            prepareCirclePoints()
            invalidate()
        }

    var strokeWidth: Int
        get() = mStrokeWidth
        set(strokeWidth) {
            mStrokeWidth = strokeWidth
            invalidate()
        }

    var circleSpacing: Int
        get() = mCircleSpacing
        set(circleSpacing) {
            mCircleSpacing = circleSpacing
            prepareCirclePoints()
            invalidate()
        }

    init {
        init(context)
    }

    private fun init(context: Context) {
        mTouchSlop = ViewConfiguration.get(context).scaledTouchSlop
        mRadius = UIUtil.dip2px(context, 3.0)
        mCircleSpacing = UIUtil.dip2px(context, 8.0)
        mStrokeWidth = UIUtil.dip2px(context, 1.0)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec))
    }

    private fun measureWidth(widthMeasureSpec: Int): Int {
        val mode = View.MeasureSpec.getMode(widthMeasureSpec)
        val width = View.MeasureSpec.getSize(widthMeasureSpec)
        var result = 0
        when (mode) {
            View.MeasureSpec.EXACTLY -> result = width
            View.MeasureSpec.AT_MOST, View.MeasureSpec.UNSPECIFIED -> result = circleCount * mRadius * 2 + (circleCount - 1) * mCircleSpacing + paddingLeft + paddingRight + mStrokeWidth * 2
            else -> {
            }
        }
        return result
    }

    private fun measureHeight(heightMeasureSpec: Int): Int {
        val mode = View.MeasureSpec.getMode(heightMeasureSpec)
        val height = View.MeasureSpec.getSize(heightMeasureSpec)
        var result = 0
        when (mode) {
            View.MeasureSpec.EXACTLY -> result = height
            View.MeasureSpec.AT_MOST, View.MeasureSpec.UNSPECIFIED -> result = mRadius * 2 + mStrokeWidth * 2 + paddingTop + paddingBottom
            else -> {
            }
        }
        return result
    }

    override fun onDraw(canvas: Canvas) {
        mPaint.color = circleColor
        drawCircles(canvas)
        drawIndicator(canvas)
    }

    private fun drawCircles(canvas: Canvas) {
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = mStrokeWidth.toFloat()
        var i = 0
        val j = mCirclePoints.size
        while (i < j) {
            val pointF = mCirclePoints[i]
            canvas.drawCircle(pointF.x, pointF.y, mRadius.toFloat(), mPaint)
            i++
        }
    }

    private fun drawIndicator(canvas: Canvas) {
        mPaint.style = Paint.Style.FILL
        if (mCirclePoints.size > 0) {
            canvas.drawCircle(mIndicatorX, (height / 2.0f + 0.5f).toInt().toFloat(), mRadius.toFloat(), mPaint)
        }
    }

    private fun prepareCirclePoints() {
        mCirclePoints.clear()
        if (circleCount > 0) {
            val y = (height / 2.0f + 0.5f).toInt()
            val centerSpacing = mRadius * 2 + mCircleSpacing
            var startX = mRadius + (mStrokeWidth / 2.0f + 0.5f).toInt() + paddingLeft
            for (i in 0 until circleCount) {
                val pointF = PointF(startX.toFloat(), y.toFloat())
                mCirclePoints.add(pointF)
                startX += centerSpacing
            }
            mIndicatorX = mCirclePoints[mCurrentIndex].x
        }
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        if (isFollowTouch) {
            if (mCirclePoints.isEmpty()) {
                return
            }

            val currentPosition = Math.min(mCirclePoints.size - 1, position)
            val nextPosition = Math.min(mCirclePoints.size - 1, position + 1)
            val current = mCirclePoints[currentPosition]
            val next = mCirclePoints[nextPosition]

            mIndicatorX = current.x + (next.x - current.x) * startInterpolator!!.getInterpolation(positionOffset)

            invalidate()
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> if (isTouchable) {
                mDownX = x
                mDownY = y
                return true
            }
            MotionEvent.ACTION_UP -> if (circleClickListener != null) {
                if (Math.abs(x - mDownX) <= mTouchSlop && Math.abs(y - mDownY) <= mTouchSlop) {
                    var max = java.lang.Float.MAX_VALUE
                    var index = 0
                    for (i in mCirclePoints.indices) {
                        val pointF = mCirclePoints[i]
                        val offset = Math.abs(pointF.x - x)
                        if (offset < max) {
                            max = offset
                            index = i
                        }
                    }
                    circleClickListener!!.onClick(index)
                }
            }
            else -> {
            }
        }
        return super.onTouchEvent(event)
    }

    override fun onPageSelected(position: Int) {
        mCurrentIndex = position
        if (!isFollowTouch) {
            mIndicatorX = mCirclePoints[mCurrentIndex].x
            invalidate()
        }
    }

    override fun onPageScrollStateChanged(state: Int) {}

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        prepareCirclePoints()
    }

    override fun onAttachToMagicIndicator() {}

    override fun notifyDataSetChanged() {
        prepareCirclePoints()
        invalidate()
    }

    override fun onDetachFromMagicIndicator() {}

    interface OnCircleClickListener {
        fun onClick(index: Int)
    }
}
