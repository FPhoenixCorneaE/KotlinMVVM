package com.wkz.widget.magicindicator.titleview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View

import com.wkz.widget.magicindicator.UIUtil
import com.wkz.widget.magicindicator.IMeasurablePagerTitleView

/**
 * 类似今日头条切换效果的指示器标题
 */
class ClipPagerTitleView(context: Context) : View(context), IMeasurablePagerTitleView {
    var text: String? = null
        set(text) {
            field = text
            requestLayout()
        }
    var textColor: Int = 0
        set(textColor) {
            field = textColor
            invalidate()
        }
    var clipColor: Int = 0
        set(clipColor) {
            field = clipColor
            invalidate()
        }
    private var mLeftToRight: Boolean = false
    private var mClipPercent: Float = 0.toFloat()

    private var mPaint: Paint? = null
    private val mTextBounds = Rect()

    var textSize: Float
        get() = mPaint!!.textSize
        set(textSize) {
            mPaint!!.textSize = textSize
            requestLayout()
        }

    override val contentLeft: Int
        get() {
            val contentWidth = mTextBounds.width()
            return left + width / 2 - contentWidth / 2
        }

    override val contentTop: Int
        get() {
            val metrics = mPaint!!.fontMetrics
            val contentHeight = metrics.bottom - metrics.top
            return (height / 2 - contentHeight / 2).toInt()
        }

    override val contentRight: Int
        get() {
            val contentWidth = mTextBounds.width()
            return left + width / 2 + contentWidth / 2
        }

    override val contentBottom: Int
        get() {
            val metrics = mPaint!!.fontMetrics
            val contentHeight = metrics.bottom - metrics.top
            return (height / 2 + contentHeight / 2).toInt()
        }

    init {
        init(context)
    }

    private fun init(context: Context) {
        val textSize = UIUtil.dip2px(context, 16.0)
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaint!!.textSize = textSize.toFloat()
        val padding = UIUtil.dip2px(context, 10.0)
        setPadding(padding, 0, padding, 0)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        measureTextBounds()
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec))
    }

    private fun measureWidth(widthMeasureSpec: Int): Int {
        val mode = View.MeasureSpec.getMode(widthMeasureSpec)
        val size = View.MeasureSpec.getSize(widthMeasureSpec)
        var result = size
        when (mode) {
            View.MeasureSpec.AT_MOST -> {
                val width = mTextBounds.width() + paddingLeft + paddingRight
                result = Math.min(width, size)
            }
            View.MeasureSpec.UNSPECIFIED -> result = mTextBounds.width() + paddingLeft + paddingRight
            else -> {
            }
        }
        return result
    }

    private fun measureHeight(heightMeasureSpec: Int): Int {
        val mode = View.MeasureSpec.getMode(heightMeasureSpec)
        val size = View.MeasureSpec.getSize(heightMeasureSpec)
        var result = size
        when (mode) {
            View.MeasureSpec.AT_MOST -> {
                val height = mTextBounds.height() + paddingTop + paddingBottom
                result = Math.min(height, size)
            }
            View.MeasureSpec.UNSPECIFIED -> result = mTextBounds.height() + paddingTop + paddingBottom
            else -> {
            }
        }
        return result
    }

    override fun onDraw(canvas: Canvas) {
        val x = (width - mTextBounds.width()) / 2
        val fontMetrics = mPaint!!.fontMetrics
        val y = ((height.toFloat() - fontMetrics.bottom - fontMetrics.top) / 2).toInt()

        // 画底层
        mPaint!!.color = textColor
        canvas.drawText(text!!, x.toFloat(), y.toFloat(), mPaint!!)

        // 画clip层
        canvas.save()
        if (mLeftToRight) {
            canvas.clipRect(0f, 0f, width * mClipPercent, height.toFloat())
        } else {
            canvas.clipRect(width * (1 - mClipPercent), 0f, width.toFloat(), height.toFloat())
        }
        mPaint!!.color = clipColor
        canvas.drawText(text!!, x.toFloat(), y.toFloat(), mPaint!!)
        canvas.restore()
    }

    override fun onSelected(index: Int, totalCount: Int) {}

    override fun onDeselected(index: Int, totalCount: Int) {}

    override fun onLeave(index: Int, totalCount: Int, leavePercent: Float, leftToRight: Boolean) {
        mLeftToRight = !leftToRight
        mClipPercent = 1.0f - leavePercent
        invalidate()
    }

    override fun onEnter(index: Int, totalCount: Int, enterPercent: Float, leftToRight: Boolean) {
        mLeftToRight = leftToRight
        mClipPercent = enterPercent
        invalidate()
    }

    private fun measureTextBounds() {
        mPaint!!.getTextBounds(text, 0, if (text == null) 0 else text!!.length, mTextBounds)
    }
}
