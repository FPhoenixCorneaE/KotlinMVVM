package com.wkz.widget.magicindicator.indicator

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator

import com.wkz.widget.magicindicator.helper.FragmentContainerHelper
import com.wkz.widget.magicindicator.IPagerIndicator
import com.wkz.widget.magicindicator.model.PositionData

/**
 * 通用的indicator，支持外面设置Drawable
 */
class CommonPagerIndicator(context: Context) : View(context), IPagerIndicator {

    /**
     * 默认为MODE_MATCH_EDGE模式
     */
    var mode: Int = 0
        set(mode) = if (mode == MODE_EXACTLY || mode == MODE_MATCH_EDGE || mode == MODE_WRAP_CONTENT) {
            field = mode
        } else {
            throw IllegalArgumentException("mode $mode not supported.")
        }
    var indicatorDrawable: Drawable? = null

    /**
     * 控制动画
     */
    var startInterpolator: Interpolator = LinearInterpolator()
    var endInterpolator: Interpolator = LinearInterpolator()

    var drawableHeight: Float = 0.toFloat()
    var drawableWidth: Float = 0.toFloat()
    var yOffset: Float = 0.toFloat()
    var xOffset: Float = 0.toFloat()

    private var mPositionDataList: List<PositionData>? = null
    private val mDrawableRect = Rect()

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        if (indicatorDrawable == null) {
            return
        }

        if (mPositionDataList == null || mPositionDataList!!.isEmpty()) {
            return
        }

        // 计算锚点位置
        val current = FragmentContainerHelper.getImitativePositionData(mPositionDataList!!, position)
        val next = FragmentContainerHelper.getImitativePositionData(mPositionDataList!!, position + 1)

        val leftX: Float
        val nextLeftX: Float
        val rightX: Float
        val nextRightX: Float
        if (mode == MODE_MATCH_EDGE) {
            leftX = current.mLeft + xOffset
            nextLeftX = next.mLeft + xOffset
            rightX = current.mRight - xOffset
            nextRightX = next.mRight - xOffset
            mDrawableRect.top = yOffset.toInt()
            mDrawableRect.bottom = (height - yOffset).toInt()
        } else if (mode == MODE_WRAP_CONTENT) {
            leftX = current.mContentLeft + xOffset
            nextLeftX = next.mContentLeft + xOffset
            rightX = current.mContentRight - xOffset
            nextRightX = next.mContentRight - xOffset
            mDrawableRect.top = (current.mContentTop - yOffset).toInt()
            mDrawableRect.bottom = (current.mContentBottom + yOffset).toInt()
        } else {    // MODE_EXACTLY
            leftX = current.mLeft + (current.width() - drawableWidth) / 2
            nextLeftX = next.mLeft + (next.width() - drawableWidth) / 2
            rightX = current.mLeft + (current.width() + drawableWidth) / 2
            nextRightX = next.mLeft + (next.width() + drawableWidth) / 2
            mDrawableRect.top = (height.toFloat() - drawableHeight - yOffset).toInt()
            mDrawableRect.bottom = (height - yOffset).toInt()
        }

        mDrawableRect.left = (leftX + (nextLeftX - leftX) * startInterpolator.getInterpolation(positionOffset)).toInt()
        mDrawableRect.right = (rightX + (nextRightX - rightX) * endInterpolator.getInterpolation(positionOffset)).toInt()
        indicatorDrawable!!.bounds = mDrawableRect

        invalidate()
    }

    override fun onPageSelected(position: Int) {}

    override fun onPageScrollStateChanged(state: Int) {}

    override fun onDraw(canvas: Canvas) {
        if (indicatorDrawable != null) {
            indicatorDrawable!!.draw(canvas)
        }
    }

    override fun onPositionDataProvide(dataList: List<PositionData>) {
        mPositionDataList = dataList
    }

    companion object {
        /**
         * drawable宽度 == title宽度 - 2 * mXOffset
         */
        const val MODE_MATCH_EDGE = 0
        /**
         * drawable宽度 == title内容宽度 - 2 * mXOffset
         */
        const val MODE_WRAP_CONTENT = 1
        const val MODE_EXACTLY = 2
    }
}
