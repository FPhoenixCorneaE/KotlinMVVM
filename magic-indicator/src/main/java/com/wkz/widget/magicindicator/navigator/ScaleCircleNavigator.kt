package com.wkz.widget.magicindicator.navigator

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.util.SparseArray
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator

import com.wkz.widget.magicindicator.helper.NavigatorHelper
import com.wkz.widget.magicindicator.IPagerNavigator
import com.wkz.widget.magicindicator.helper.ArgbEvaluatorHolder
import com.wkz.widget.magicindicator.UIUtil

import java.util.ArrayList

/**
 * 类似CircleIndicator的效果
 */
class ScaleCircleNavigator(context: Context) : View(context), IPagerNavigator, NavigatorHelper.OnNavigatorScrollListener {
    private var mMinRadius: Int = 0
    private var mMaxRadius: Int = 0
    private var mNormalCircleColor = Color.LTGRAY
    private var mSelectedCircleColor = Color.GRAY
    private var mCircleSpacing: Int = 0
    private var mCircleCount: Int = 0

    private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mCirclePoints = ArrayList<PointF>()
    private val mCircleRadiusArray = SparseArray<Float>()

    /**
     * 事件回调
     */
    private var mTouchable: Boolean = false
    private var mCircleClickListener: ScaleCircleNavigator.OnCircleClickListener? = null
    private var mDownX: Float = 0.toFloat()
    private var mDownY: Float = 0.toFloat()
    private var mTouchSlop: Int = 0

    /**
     * 是否跟随手指滑动
     */
    private var mFollowTouch = true
    private val mNavigatorHelper = NavigatorHelper()
    private var mStartInterpolator: Interpolator? = LinearInterpolator()

    init {
        init(context)
    }

    private fun init(context: Context) {
        mTouchSlop = ViewConfiguration.get(context).scaledTouchSlop
        mMinRadius = UIUtil.dip2px(context, 3.0)
        mMaxRadius = UIUtil.dip2px(context, 5.0)
        mCircleSpacing = UIUtil.dip2px(context, 8.0)
        mNavigatorHelper.setNavigatorScrollListener(this)
        mNavigatorHelper.setSkimOver(true)
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
            View.MeasureSpec.AT_MOST, View.MeasureSpec.UNSPECIFIED -> if (mCircleCount <= 0) {
                result = paddingLeft + paddingRight
            } else {
                result = (mCircleCount - 1) * mMinRadius * 2 + mMaxRadius * 2 + (mCircleCount - 1) * mCircleSpacing + paddingLeft + paddingRight
            }
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
            View.MeasureSpec.AT_MOST, View.MeasureSpec.UNSPECIFIED -> result = mMaxRadius * 2 + paddingTop + paddingBottom
            else -> {
            }
        }
        return result
    }

    override fun onDraw(canvas: Canvas) {
        var i = 0
        val j = mCirclePoints.size
        while (i < j) {
            val point = mCirclePoints[i]
            val radius = mCircleRadiusArray.get(i, mMinRadius.toFloat())
            mPaint.color = ArgbEvaluatorHolder.eval((radius - mMinRadius) / (mMaxRadius - mMinRadius), mNormalCircleColor, mSelectedCircleColor)
            canvas.drawCircle(point.x, height / 2.0f, radius, mPaint)
            i++
        }
    }

    private fun prepareCirclePoints() {
        mCirclePoints.clear()
        if (mCircleCount > 0) {
            val y = Math.round(height / 2.0f)
            val centerSpacing = mMinRadius * 2 + mCircleSpacing
            var startX = mMaxRadius + paddingLeft
            for (i in 0 until mCircleCount) {
                val pointF = PointF(startX.toFloat(), y.toFloat())
                mCirclePoints.add(pointF)
                startX += centerSpacing
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> if (mTouchable) {
                mDownX = x
                mDownY = y
                return true
            }
            MotionEvent.ACTION_UP -> if (mCircleClickListener != null) {
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
                    mCircleClickListener!!.onClick(index)
                }
            }
            else -> {
            }
        }
        return super.onTouchEvent(event)
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        mNavigatorHelper.onPageScrolled(position, positionOffset, positionOffsetPixels)
    }

    override fun onPageSelected(position: Int) {
        mNavigatorHelper.onPageSelected(position)
    }

    override fun onPageScrollStateChanged(state: Int) {
        mNavigatorHelper.onPageScrollStateChanged(state)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        prepareCirclePoints()
    }

    override fun notifyDataSetChanged() {
        prepareCirclePoints()
        requestLayout()
    }

    override fun onAttachToMagicIndicator() {}

    override fun onDetachFromMagicIndicator() {}

    fun setMinRadius(minRadius: Int) {
        mMinRadius = minRadius
        prepareCirclePoints()
        invalidate()
    }

    fun setMaxRadius(maxRadius: Int) {
        mMaxRadius = maxRadius
        prepareCirclePoints()
        invalidate()
    }

    fun setNormalCircleColor(normalCircleColor: Int) {
        mNormalCircleColor = normalCircleColor
        invalidate()
    }

    fun setSelectedCircleColor(selectedCircleColor: Int) {
        mSelectedCircleColor = selectedCircleColor
        invalidate()
    }

    fun setCircleSpacing(circleSpacing: Int) {
        mCircleSpacing = circleSpacing
        prepareCirclePoints()
        invalidate()
    }

    fun setStartInterpolator(startInterpolator: Interpolator) {
        mStartInterpolator = startInterpolator
        if (mStartInterpolator == null) {
            mStartInterpolator = LinearInterpolator()
        }
    }

    fun setCircleCount(count: Int) {
        mCircleCount = count  // 此处不调用invalidate，让外部调用notifyDataSetChanged
        mNavigatorHelper.totalCount = mCircleCount
    }

    fun setTouchable(touchable: Boolean) {
        mTouchable = touchable
    }

    fun setFollowTouch(followTouch: Boolean) {
        mFollowTouch = followTouch
    }

    fun setSkimOver(skimOver: Boolean) {
        mNavigatorHelper.setSkimOver(skimOver)
    }

    fun setCircleClickListener(circleClickListener: OnCircleClickListener) {
        if (!mTouchable) {
            mTouchable = true
        }
        mCircleClickListener = circleClickListener
    }

    override fun onEnter(index: Int, totalCount: Int, enterPercent: Float, leftToRight: Boolean) {
        if (mFollowTouch) {
            val radius = mMinRadius + (mMaxRadius - mMinRadius) * mStartInterpolator!!.getInterpolation(enterPercent)
            mCircleRadiusArray.put(index, radius)
            invalidate()
        }
    }

    override fun onLeave(index: Int, totalCount: Int, leavePercent: Float, leftToRight: Boolean) {
        if (mFollowTouch) {
            val radius = mMaxRadius + (mMinRadius - mMaxRadius) * mStartInterpolator!!.getInterpolation(leavePercent)
            mCircleRadiusArray.put(index, radius)
            invalidate()
        }
    }

    override fun onSelected(index: Int, totalCount: Int) {
        if (!mFollowTouch) {
            mCircleRadiusArray.put(index, mMaxRadius.toFloat())
            invalidate()
        }
    }

    override fun onDeselected(index: Int, totalCount: Int) {
        if (!mFollowTouch) {
            mCircleRadiusArray.put(index, mMinRadius.toFloat())
            invalidate()
        }
    }

    interface OnCircleClickListener {
        fun onClick(index: Int)
    }
}
