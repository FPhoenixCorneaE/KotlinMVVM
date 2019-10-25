package com.wkz.widget.magicindicator.indicator

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.view.View
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator

import com.wkz.widget.magicindicator.helper.FragmentContainerHelper
import com.wkz.widget.magicindicator.UIUtil
import com.wkz.widget.magicindicator.IPagerIndicator
import com.wkz.widget.magicindicator.model.PositionData

/**
 * 带有小尖角的直线指示器
 */
class TriangularPagerIndicator(context: Context) : View(context), IPagerIndicator {
    private var mPositionDataList: List<PositionData>? = null
    private var mPaint: Paint? = null
    var lineHeight: Int = 0
    var lineColor: Int = 0
    var triangleHeight: Int = 0
    var triangleWidth: Int = 0
    var isReverse: Boolean = false
    var yOffset: Float = 0.toFloat()

    private val mPath = Path()
    var startInterpolator: Interpolator? = LinearInterpolator()
        set(startInterpolator) {
            field = startInterpolator
            if (this.startInterpolator == null) {
                field = LinearInterpolator()
            }
        }
    private var mAnchorX: Float = 0.toFloat()

    init {
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaint!!.style = Paint.Style.FILL
        lineHeight = UIUtil.dip2px(context, 3.0)
        triangleWidth = UIUtil.dip2px(context, 14.0)
        triangleHeight = UIUtil.dip2px(context, 8.0)
    }

    override fun onDraw(canvas: Canvas) {
        mPaint!!.color = lineColor
        if (isReverse) {
            canvas.drawRect(0f, height.toFloat() - yOffset - triangleHeight.toFloat(), width.toFloat(), height.toFloat() - yOffset - triangleHeight.toFloat() + lineHeight, mPaint!!)
        } else {
            canvas.drawRect(0f, height.toFloat() - lineHeight.toFloat() - yOffset, width.toFloat(), height - yOffset, mPaint!!)
        }
        mPath.reset()
        if (isReverse) {
            mPath.moveTo(mAnchorX - triangleWidth / 2, height.toFloat() - yOffset - triangleHeight.toFloat())
            mPath.lineTo(mAnchorX, height - yOffset)
            mPath.lineTo(mAnchorX + triangleWidth / 2, height.toFloat() - yOffset - triangleHeight.toFloat())
        } else {
            mPath.moveTo(mAnchorX - triangleWidth / 2, height - yOffset)
            mPath.lineTo(mAnchorX, height.toFloat() - triangleHeight.toFloat() - yOffset)
            mPath.lineTo(mAnchorX + triangleWidth / 2, height - yOffset)
        }
        mPath.close()
        canvas.drawPath(mPath, mPaint!!)
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        if (mPositionDataList == null || mPositionDataList!!.isEmpty()) {
            return
        }

        // 计算锚点位置
        val current = FragmentContainerHelper.getImitativePositionData(mPositionDataList!!, position)
        val next = FragmentContainerHelper.getImitativePositionData(mPositionDataList!!, position + 1)

        val leftX = (current.mLeft + (current.mRight - current.mLeft) / 2).toFloat()
        val rightX = (next.mLeft + (next.mRight - next.mLeft) / 2).toFloat()

        mAnchorX = leftX + (rightX - leftX) * startInterpolator!!.getInterpolation(positionOffset)

        invalidate()
    }

    override fun onPageSelected(position: Int) {}

    override fun onPageScrollStateChanged(state: Int) {}

    override fun onPositionDataProvide(dataList: List<PositionData>) {
        mPositionDataList = dataList
    }
}
