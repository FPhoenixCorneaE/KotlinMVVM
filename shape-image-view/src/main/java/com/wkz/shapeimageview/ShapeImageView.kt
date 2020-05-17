package com.wkz.shapeimageview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.annotation.IntDef
import androidx.core.content.ContextCompat
import com.bumptech.glide.TransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.wkz.shapeimageview.progress.OnGlideImageViewListener
import com.wkz.shapeimageview.progress.OnProgressListener

/**
 * 形状图像视图,圆形、矩形（圆角矩形）
 *
 * @author Administrator
 */
class ShapeImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ImageView(context, attrs, defStyleAttr) {

    /**
     * 图片的宽高
     */
    private var mWidth = 0
    private var mHeight = 0

    /**
     * 边框：画笔、颜色、宽度、路径
     */
    private val mBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mBorderColor = 0
    private var mBorderWidth = 0f
    private var mBorderPath = Path()

    /**
     * 圆角、左上角、右上角、左下角、右下角弧度
     */
    private var mRadius = 0f
    private var mRadiusTopLeft = 0f
    private var mRadiusTopRight = 0f
    private var mRadiusBottomLeft = 0f
    private var mRadiusBottomRight = 0f

    /**
     * 圆角弧度数组
     */
    private var mRadii: FloatArray = floatArrayOf()

    /**
     * 图片形状类型,默认为矩形
     */
    private var mShapeType = ShapeType.RECTANGLE

    /**
     * 图片加载器
     */
    private lateinit var mImageLoader: GlideImageLoader

    /**
     * 矩形范围
     */
    private var mRectF = RectF()

    /**
     * 图片：画笔、路径
     */
    private val mDrawablePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mDrawablePath = Path()

    @IntDef(
        ShapeType.RECTANGLE,
        ShapeType.CIRCLE
    )
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    annotation class ShapeType {
        companion object {
            /**
             * 矩形
             */
            const val RECTANGLE = 0

            /**
             * 圆形
             */
            const val CIRCLE = 1
        }
    }

    private fun initAttr(
        context: Context,
        attrs: AttributeSet?
    ) {
        attrs?.let {
            val array = context.obtainStyledAttributes(it, R.styleable.ShapeImageView)
            try {
                mBorderWidth = array.getDimension(
                    R.styleable.ShapeImageView_siv_border_width,
                    0f
                )
                mBorderColor = array.getColor(
                    R.styleable.ShapeImageView_siv_border_color,
                    0
                )
                mRadius = array.getDimension(
                    R.styleable.ShapeImageView_siv_radius,
                    0f
                )
                mRadiusTopLeft = array.getDimension(
                    R.styleable.ShapeImageView_siv_radius_top_left,
                    0f
                )
                mRadiusTopRight = array.getDimension(
                    R.styleable.ShapeImageView_siv_radius_top_right,
                    0f
                )
                mRadiusBottomLeft = array.getDimension(
                    R.styleable.ShapeImageView_siv_radius_bottom_left,
                    0f
                )
                mRadiusBottomRight = array.getDimension(
                    R.styleable.ShapeImageView_siv_radius_bottom_right,
                    0f
                )
                mShapeType = array.getInteger(
                    R.styleable.ShapeImageView_siv_shape_type,
                    ShapeType.RECTANGLE
                )
            } finally {
                array.recycle()
            }
        }
        mImageLoader = GlideImageLoader(this)
        initRadii()
    }

    /**
     * 初始化圆角弧度数组
     */
    private fun initRadii() {
        /* 向路径中添加圆角矩形。radii数组定义圆角矩形的四个圆角的x,y半径。*/
        /* 圆角的半径，依次为左上角xy半径，右上角，右下角，左下角 */
        if (mShapeType == ShapeType.RECTANGLE) {
            mRadii = when {
                mRadius > 0 -> {
                    floatArrayOf(
                        mRadius, mRadius,
                        mRadius, mRadius,
                        mRadius, mRadius,
                        mRadius, mRadius
                    )
                }
                else -> {
                    floatArrayOf(
                        mRadiusTopLeft, mRadiusTopLeft,
                        mRadiusTopRight, mRadiusTopRight,
                        mRadiusBottomRight, mRadiusBottomRight,
                        mRadiusBottomLeft, mRadiusBottomLeft
                    )
                }
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mWidth = measuredWidth
        mHeight = measuredHeight
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w
        mHeight = h
    }

    override fun onDraw(canvas: Canvas) {
        if (drawable == null || mWidth == 0 || mHeight == 0) {
            return
        }
        drawDrawable(canvas)
        drawBorder(canvas)
    }

    /**
     * 绘制图片
     *
     * @param canvas    画布
     */
    private fun drawDrawable(canvas: Canvas) {
        val saveCount = canvas.save()
        mDrawablePath.rewind()
        if (mShapeType == ShapeType.RECTANGLE) {
            mRectF.left = mBorderWidth
            mRectF.top = mBorderWidth
            mRectF.right = mWidth - mBorderWidth
            mRectF.bottom = mHeight - mBorderWidth
            // 添加圆角矩形路径,Path.Direction.CW-顺时针;Path.Direction.CCW-逆时针
            mDrawablePath.addRoundRect(mRectF, mRadii, Path.Direction.CW)
        } else {
            // 添加圆形路径,Path.Direction.CW-顺时针;Path.Direction.CCW-逆时针
            mDrawablePath.addCircle(
                mWidth.toFloat() / 2,
                mHeight.toFloat() / 2,
                mWidth.toFloat() / 2 - mBorderWidth,
                Path.Direction.CW
            )
        }
        // 剪裁图形
        canvas.clipPath(mDrawablePath)
        // 合并图像矩阵
        canvas.concat(imageMatrix)
        // 绘制图片到画布
        drawable.draw(canvas)
        mDrawablePath.close()
        canvas.restoreToCount(saveCount)
    }

    /**
     * 绘制边框
     *
     * @param canvas 画布
     */
    private fun drawBorder(canvas: Canvas) {
        if (mBorderWidth > 0) {
            val saveCount = canvas.save()
            mBorderPaint.strokeWidth = mBorderWidth
            mBorderPaint.style = Paint.Style.STROKE
            mBorderPaint.color = mBorderColor
            if (mShapeType == ShapeType.RECTANGLE) {
                mRectF.left = mBorderWidth / 2
                mRectF.top = mBorderWidth / 2
                mRectF.right = mWidth - mBorderWidth / 2
                mRectF.bottom = mHeight - mBorderWidth / 2
                mBorderPath.rewind()
                mBorderPath.addRoundRect(mRectF, mRadii, Path.Direction.CW)
                // 绘制圆角边框
                canvas.drawPath(mBorderPath, mBorderPaint)
                mBorderPath.close()
            } else {
                // 绘制圆形边框
                canvas.drawCircle(
                    mWidth.toFloat() / 2,
                    mHeight.toFloat() / 2,
                    (mWidth.toFloat() - mBorderWidth) / 2,
                    mBorderPaint
                )
            }
            canvas.restoreToCount(saveCount)
        }
    }

    /**
     * 设置边框颜色
     *
     * @param id 颜色资源id
     */
    fun setBorderColor(@ColorRes id: Int) {
        mBorderColor = ContextCompat.getColor(context, id)
        postInvalidate()
    }

    /**
     * 设置边框宽度
     *
     * @param dpValue 边框宽度,单位为DP
     */
    fun setBorderWidth(dpValue: Int) {
        mBorderWidth = DisplayUtils.dip2px(context, dpValue.toFloat())
        postInvalidate()
    }

    /**
     * 设置圆角半径
     *
     * @param dpValue 圆角半径
     */
    fun setRadius(dpValue: Int) {
        mRadius = DisplayUtils.dip2px(context, dpValue.toFloat())
        postInvalidate()
    }

    /**
     * 设置圆角半径
     *
     * @param radii 圆角半径
     */
    fun setRadii(radii: FloatArray) {
        mRadii = radii
        postInvalidate()
    }

    /**
     * 设置形状类型
     *
     * @param shapeType 形状类型[ShapeType]
     */
    fun setShapeType(@ShapeType shapeType: Int) {
        mShapeType = shapeType
        postInvalidate()
    }

    fun load(obj: Any?, vararg placeholder: Int): ShapeImageView {
        mImageLoader!!.load(obj, *placeholder)
        return this
    }

    fun load(obj: Any?, options: RequestOptions): ShapeImageView {
        mImageLoader!!.load(obj, options)
        return this
    }

    fun load(
        obj: Any?,
        transitionOptions: TransitionOptions<*, in Drawable?>?,
        vararg placeholder: Int
    ): ShapeImageView {
        mImageLoader!!.load(obj, transitionOptions, *placeholder)
        return this
    }

    fun listener(listener: OnGlideImageViewListener?): ShapeImageView {
        mImageLoader!!.setOnGlideImageViewListener(listener)
        return this
    }

    fun listener(listener: OnProgressListener?): ShapeImageView {
        mImageLoader!!.setOnProgressListener(listener)
        return this
    }

    init {
        initAttr(context, attrs)
    }
}