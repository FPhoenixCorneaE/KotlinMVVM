package com.fphoenixcorneae.animated_bottom_view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import com.fphoenixcorneae.util.SizeUtil
import kotlin.math.max
import kotlin.math.min

/**
 * @desc：贝塞尔凹形曲线
 */
class ConcaveView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {


    private var _fraction: Float = 0f
    private var _x: Float = 0f

    private var _curveWidth0 = 0f
    private var _curveWidth1 = 0f
    private var _curveDepth0 = 0f
    private var _curveDepth1 = 0f
    private var _curveControlA0 = 0f
    private var _curveControlA1 = 0f
    private var _curveControlB0 = 0f
    private var _curveControlB1 = 0f

    private var _paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var _path = Path()

    init {
        _curveWidth0 = SizeUtil.pxToPx(340.0f, 1080)
        _curveWidth1 = SizeUtil.pxToPx(180.0f, 1080)
        _curveDepth0 = SizeUtil.pxToPx(120.0f, 1080)
        _curveDepth1 = SizeUtil.pxToPx(75.0f, 1080)
        _curveControlA0 = SizeUtil.pxToPx(80.0f, 1080)
        _curveControlA1 = SizeUtil.pxToPx(40.0f, 1080)
        _curveControlB0 = SizeUtil.pxToPx(115.0f, 1080)
        _curveControlB1 = SizeUtil.pxToPx(50.0f, 1080)
    }

    fun setParams(
        width0: Float,
        width1: Float,
        depth0: Float,
        depth1: Float,
        controlA0: Float,
        controlA1: Float,
        controlB0: Float,
        controlB1: Float
    ) {
        _curveWidth0 = width0
        _curveWidth1 = width1
        _curveDepth0 = depth0
        _curveDepth1 = depth1
        _curveControlA0 = controlA0
        _curveControlA1 = controlA1
        _curveControlB0 = controlB0
        _curveControlB1 = controlB1
    }

    /**
     * 根据球的上下位置，画贝塞尔曲线。仔细观察移动的过程中 凹的形状是不一样的
     * 球在上面的时候凹形大，球在下面的时候凹形状小
     */
    fun setFractionAndX(fraction: Float, x: Float) {
        _fraction = fraction
        _x = x
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        _path.reset()

        val width = _curveWidth0 + _fraction * (_curveWidth1 - _curveWidth0)
        val depth = _curveDepth0 + _fraction * (_curveDepth1 - _curveDepth0)
        val controlA = _curveControlA0 + _fraction * (_curveControlA1 - _curveControlA0)
        val controlB = _curveControlB0 + _fraction * (_curveControlB1 - _curveControlB0)

        val cl = _x - width * 0.5f
        val cr = _x + width * 0.5f

        val left = min(cl, 0.0f)
        val right = max(cr, getWidth().toFloat())
        val bottom = height.toFloat()

        _path.moveTo(cl, 0.0f)
        _path.cubicTo(cl + controlA, 0.0f, _x - controlB, depth, _x, depth)
        _path.cubicTo(_x + controlB, depth, cr - controlA, 0.0f, cr, 0.0f)
        if (right != cr) {
            _path.lineTo(right, 0.0f)
        }
        _path.lineTo(right, bottom)
        _path.lineTo(left, bottom)
        _path.lineTo(left, 0.0f)
        if (left != cl) {
            _path.lineTo(cl, 0.0f)
        }

        canvas.drawPath(_path, _paint)
    }

    init {
        _paint.color = -0x1
        _paint.style = Paint.Style.FILL
    }
}
