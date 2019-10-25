package com.wkz.widget.magicindicator.indicator

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View

import com.wkz.widget.magicindicator.UIUtil
import com.wkz.widget.magicindicator.IPagerIndicator
import com.wkz.widget.magicindicator.model.PositionData

/**
 * 非手指跟随的小圆点指示器
 */
class DotPagerIndicator(context: Context) : View(context), IPagerIndicator {
    private var mDataList: List<PositionData>? = null
    private var mRadius: Float = 0.toFloat()
    private var mYOffset: Float = 0.toFloat()
    private var mDotColor: Int = 0

    private var mCircleCenterX: Float = 0.toFloat()
    private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    var radius: Float
        get() = mRadius
        set(radius) {
            mRadius = radius
            invalidate()
        }

    var yOffset: Float
        get() = mYOffset
        set(yOffset) {
            mYOffset = yOffset
            invalidate()
        }

    var dotColor: Int
        get() = mDotColor
        set(dotColor) {
            mDotColor = dotColor
            invalidate()
        }

    init {
        mRadius = UIUtil.dip2px(context, 3.0).toFloat()
        mYOffset = UIUtil.dip2px(context, 3.0).toFloat()
        mDotColor = Color.WHITE
    }

    override fun onPageSelected(position: Int) {
        if (mDataList == null || mDataList!!.isEmpty()) {
            return
        }
        val data = mDataList!![position]
        mCircleCenterX = (data.mLeft + data.width() / 2).toFloat()
        invalidate()
    }

    override fun onPositionDataProvide(dataList: List<PositionData>) {
        mDataList = dataList
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        mPaint.color = mDotColor
        canvas.drawCircle(mCircleCenterX, height.toFloat() - mYOffset - mRadius, mRadius, mPaint)
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

    override fun onPageScrollStateChanged(state: Int) {}
}
