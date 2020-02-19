package com.wkz.shinebutton

import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet

open class PorterShapeImageView : AbstractPorterImageView {
    private var shape: Drawable? = null
    private var mMatrix: Matrix? = null
    private var drawMatrix: Matrix? = null

    constructor(context: Context) : super(context) {
        setup(context, null, 0)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setup(context, attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        setup(context, attrs, defStyle)
    }

    private fun setup(context: Context, attrs: AttributeSet?, defStyle: Int) {
        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.PorterShapeImageView, defStyle, 0)
            shape = typedArray.getDrawable(R.styleable.PorterShapeImageView_shinebutton_shape)
            typedArray.recycle()
        }
        mMatrix = Matrix()
    }

    fun setShape(drawable: Drawable?) {
        shape = drawable
        invalidate()
    }

    override fun paintMaskCanvas(maskCanvas: Canvas, maskPaint: Paint?, width: Int, height: Int) {
        if (shape != null) {
            if (shape is BitmapDrawable) {
                configureBitmapBounds(getWidth(), getHeight())
                if (drawMatrix != null) {
                    val drawableSaveCount = maskCanvas.saveCount
                    maskCanvas.save()
                    maskCanvas.concat(mMatrix)
                    shape?.draw(maskCanvas)
                    maskCanvas.restoreToCount(drawableSaveCount)
                    return
                }
            }
            shape!!.setBounds(0, 0, getWidth(), getHeight())
            shape!!.draw(maskCanvas)
        }
    }

    private fun configureBitmapBounds(viewWidth: Int, viewHeight: Int) {
        drawMatrix = null
        val drawableWidth = shape!!.intrinsicWidth
        val drawableHeight = shape!!.intrinsicHeight
        val fits = viewWidth == drawableWidth && viewHeight == drawableHeight
        if (drawableWidth > 0 && drawableHeight > 0 && !fits) {
            shape!!.setBounds(0, 0, drawableWidth, drawableHeight)
            val widthRatio = viewWidth.toFloat() / drawableWidth.toFloat()
            val heightRatio = viewHeight.toFloat() / drawableHeight.toFloat()
            val scale = Math.min(widthRatio, heightRatio)
            val dx: Float = (viewWidth - drawableWidth * scale) * 0.5f + 0.5f
            val dy: Float = (viewHeight - drawableHeight * scale) * 0.5f + 0.5f
            mMatrix!!.setScale(scale, scale)
            mMatrix!!.postTranslate(dx, dy)
        }
    }
}