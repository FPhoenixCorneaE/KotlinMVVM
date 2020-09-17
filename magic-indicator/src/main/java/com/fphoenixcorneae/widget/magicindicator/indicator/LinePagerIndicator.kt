package com.fphoenixcorneae.widget.magicindicator.indicator

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.view.View
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import com.fphoenixcorneae.widget.magicindicator.IPagerIndicator
import com.fphoenixcorneae.widget.magicindicator.UIUtil
import com.fphoenixcorneae.widget.magicindicator.helper.ArgbEvaluatorHolder
import com.fphoenixcorneae.widget.magicindicator.helper.FragmentContainerHelper
import com.fphoenixcorneae.widget.magicindicator.model.PositionData
import kotlin.math.abs

/**
 * 直线viewpager指示器，带颜色渐变
 */
class LinePagerIndicator(context: Context) : View(context), IPagerIndicator {

    /**
     * 默认为MODE_MATCH_EDGE模式
     */
    var mode: Int = 0
        set(mode) = if (mode == MODE_EXACTLY || mode == MODE_MATCH_EDGE || mode == MODE_WRAP_CONTENT) {
            field = mode
        } else {
            throw IllegalArgumentException("mode $mode not supported.")
        }

    /**
     * 控制动画
     */
    var startInterpolator: Interpolator? = LinearInterpolator()
        set(startInterpolator) {
            field = startInterpolator
            if (this.startInterpolator == null) {
                field = LinearInterpolator()
            }
        }
    var endInterpolator: Interpolator? = LinearInterpolator()
        set(endInterpolator) {
            field = endInterpolator
            if (this.endInterpolator == null) {
                field = LinearInterpolator()
            }
        }

    /**
     * 相对于底部的偏移量，如果你想让直线位于title上方，设置它即可
     */
    var yOffset: Float = 0.toFloat()
    var lineHeight: Float = 0.toFloat()
    var xOffset: Float = 0.toFloat()
    var lineWidth: Float = 0.toFloat()
    var roundRadius: Float = 0.toFloat()

    var paint: Paint? = null
        private set
    private var mPositionDataList: List<PositionData>? = null
    var mColors: List<Int>? = null
        private set

    private val mLineRect = RectF()

    init {
        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint!!.style = Paint.Style.FILL
        lineHeight = UIUtil.dip2px(context, 3.0).toFloat()
        lineWidth = UIUtil.dip2px(context, 10.0).toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawRoundRect(mLineRect, roundRadius, roundRadius, paint!!)
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        if (mPositionDataList == null || mPositionDataList!!.isEmpty()) {
            return
        }

        // 计算颜色
        if (mColors != null && mColors!!.isNotEmpty()) {
            val currentColor = mColors!![abs(position) % mColors!!.size]
            val nextColor = mColors!![abs(position + 1) % mColors!!.size]
            val color = ArgbEvaluatorHolder.eval(positionOffset, currentColor, nextColor)
            paint!!.color = color
        }

        // 计算锚点位置
        val current =
            FragmentContainerHelper.getImitativePositionData(mPositionDataList!!, position)
        val next =
            FragmentContainerHelper.getImitativePositionData(mPositionDataList!!, position + 1)

        val leftX: Float
        val nextLeftX: Float
        val rightX: Float
        val nextRightX: Float
        when (mode) {
            MODE_MATCH_EDGE -> {
                leftX = current.mLeft + xOffset
                nextLeftX = next.mLeft + xOffset
                rightX = current.mRight - xOffset
                nextRightX = next.mRight - xOffset
            }
            MODE_WRAP_CONTENT -> {
                leftX = current.mContentLeft + xOffset
                nextLeftX = next.mContentLeft + xOffset
                rightX = current.mContentRight - xOffset
                nextRightX = next.mContentRight - xOffset
            }
            else -> {
                // MODE_EXACTLY
                leftX = current.mLeft + (current.width() - lineWidth) / 2
                nextLeftX = next.mLeft + (next.width() - lineWidth) / 2
                rightX = current.mLeft + (current.width() + lineWidth) / 2
                nextRightX = next.mLeft + (next.width() + lineWidth) / 2
            }
        }

        mLineRect.left =
            leftX + (nextLeftX - leftX) * startInterpolator!!.getInterpolation(positionOffset)
        mLineRect.right =
            rightX + (nextRightX - rightX) * endInterpolator!!.getInterpolation(positionOffset)
        mLineRect.top = height.toFloat() - lineHeight - yOffset
        mLineRect.bottom = height - yOffset

        invalidate()
    }

    override fun onPageSelected(position: Int) {}

    override fun onPageScrollStateChanged(state: Int) {}

    override fun onPositionDataProvide(dataList: List<PositionData>) {
        mPositionDataList = dataList
    }

    fun setColors(vararg colors: Int) {
        this.mColors = listOf(*colors.toTypedArray())
    }

    companion object {
        /**
         * 直线宽度 == title宽度 - 2 * mXOffset
         */
        const val MODE_MATCH_EDGE = 0
        /**
         * 直线宽度 == title内容宽度 - 2 * mXOffset
         */
        const val MODE_WRAP_CONTENT = 1
        /**
         * 直线宽度 == mLineWidth
         */
        const val MODE_EXACTLY = 2
    }
}
