package com.wkz.shapeimageview

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
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
    private var mBorderWidth = 0
    private var mBorderPath = Path()

    /**
     * 圆角、左上角、右上角、左下角、右下角弧度
     */
    private var mRadius = 0
    private var mRadiusTopLeft = 0
    private var mRadiusTopRight = 0
    private var mRadiusBottomLeft = 0
    private var mRadiusBottomRight = 0

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
    private var mImageLoader: GlideImageLoader? = null

    /**
     * 圆角矩形
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

    private fun init(
        context: Context,
        attrs: AttributeSet?
    ) {
        if (attrs != null) {
            val array = context.obtainStyledAttributes(attrs, R.styleable.ShapeImageView)
            try {
                mBorderWidth =
                    array.getDimensionPixelOffset(R.styleable.ShapeImageView_siv_border_width, 0)
                mBorderColor = array.getColor(R.styleable.ShapeImageView_siv_border_color, 0)
                mRadius = array.getDimensionPixelOffset(R.styleable.ShapeImageView_siv_radius, 0)
                mRadiusTopLeft =
                    array.getDimensionPixelOffset(R.styleable.ShapeImageView_siv_radius_top_left, 0)
                mRadiusTopRight = array.getDimensionPixelOffset(
                    R.styleable.ShapeImageView_siv_radius_top_right,
                    0
                )
                mRadiusBottomLeft = array.getDimensionPixelOffset(
                    R.styleable.ShapeImageView_siv_radius_bottom_left,
                    0
                )
                mRadiusBottomRight = array.getDimensionPixelOffset(
                    R.styleable.ShapeImageView_siv_radius_bottom_right,
                    0
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
                        mRadius.toFloat(), mRadius.toFloat(),
                        mRadius.toFloat(), mRadius.toFloat(),
                        mRadius.toFloat(), mRadius.toFloat(),
                        mRadius.toFloat(), mRadius
                            .toFloat()
                    )
                }
                else -> {
                    floatArrayOf(
                        mRadiusTopLeft.toFloat(), mRadiusTopLeft.toFloat(),
                        mRadiusTopRight.toFloat(), mRadiusTopRight.toFloat(),
                        mRadiusBottomRight.toFloat(), mRadiusBottomRight.toFloat(),
                        mRadiusBottomLeft.toFloat(), mRadiusBottomLeft
                            .toFloat()
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
        val drawable = drawable
        if (drawable == null || width == 0 || height == 0) {
            return
        }
        drawDrawable(canvas, getBitmapFromDrawable(drawable))
        drawBorder(canvas)
    }

    /**
     * 绘制图片
     *
     * @param canvas    画布
     * @param srcBitmap 位图
     */
    private fun drawDrawable(
        canvas: Canvas,
        srcBitmap: Bitmap
    ) {
        canvas.saveLayer(
            0f,
            0f,
            mWidth.toFloat(),
            mHeight.toFloat(),
            null
        )
        var bitmap = srcBitmap
        mDrawablePaint.color = Color.WHITE
        if (mShapeType == ShapeType.RECTANGLE) {
            mRectF.left = mBorderWidth.toFloat() / 2
            mRectF.top = mBorderWidth.toFloat() / 2
            mRectF.right = width - mBorderWidth.toFloat() / 2
            mRectF.bottom = height - mBorderWidth.toFloat() / 2
            mDrawablePath.rewind()
            mDrawablePath.addRoundRect(mRectF, mRadii, Path.Direction.CW)
            canvas.drawPath(mDrawablePath, mDrawablePaint)
            mDrawablePath.close()
        } else {
            canvas.drawCircle(
                mWidth.toFloat() / 2,
                mHeight.toFloat() / 2,
                mWidth.toFloat() / 2 - mBorderWidth,
                mDrawablePaint
            )
        }

        // SRC_IN 只显示两层图像交集部分的上层图像
        mDrawablePaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)

        // Bitmap缩放
        val scaleWidth = width.toFloat() / bitmap.width
        val scaleHeight = height.toFloat() / bitmap.height
        val matrix = Matrix()
        matrix.postScale(scaleWidth, scaleHeight)
        bitmap = Bitmap.createBitmap(
            bitmap,
            0,
            0,
            bitmap.width,
            bitmap.height,
            matrix,
            true
        )
        canvas.drawBitmap(bitmap, 0f, 0f, mDrawablePaint)
        canvas.restore()
    }

    /**
     * 绘制边框
     *
     * @param canvas 画布
     */
    private fun drawBorder(canvas: Canvas) {
        if (mBorderWidth > 0) {
            canvas.save()
            mBorderPaint.strokeWidth = mBorderWidth.toFloat()
            mBorderPaint.style = Paint.Style.STROKE
            mBorderPaint.color = mBorderColor
            if (mShapeType == ShapeType.RECTANGLE) {
                mRectF.left = mBorderWidth.toFloat() / 2
                mRectF.top = mBorderWidth.toFloat() / 2
                mRectF.right = width - mBorderWidth.toFloat() / 2
                mRectF.bottom = height - mBorderWidth.toFloat() / 2
                mBorderPath.rewind()
                mBorderPath.addRoundRect(mRectF, mRadii, Path.Direction.CW)
                canvas.drawPath(mBorderPath, mBorderPaint)
            } else {
                canvas.drawCircle(
                    mWidth.toFloat() / 2,
                    mHeight.toFloat() / 2,
                    (mWidth - mBorderWidth).toFloat() / 2,
                    mBorderPaint
                )
            }
            canvas.restore()
        }
    }

    /**
     * 获取Bitmap
     *
     * @param drawable 图片
     * @return Bitmap
     */
    private fun getBitmapFromDrawable(drawable: Drawable): Bitmap {
        return try {
            val bitmap: Bitmap = when (drawable) {
                is BitmapDrawable -> {
                    return drawable.bitmap
                }
                is ColorDrawable -> {
                    Bitmap.createBitmap(
                        1,
                        1,
                        Bitmap.Config.RGB_565
                    )
                }
                else -> {
                    Bitmap.createBitmap(
                        drawable.intrinsicWidth,
                        drawable.intrinsicHeight,
                        Bitmap.Config.RGB_565
                    )
                }
            }
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        } catch (e: OutOfMemoryError) {
            e.printStackTrace()
            Bitmap.createBitmap(
                1,
                1,
                Bitmap.Config.RGB_565
            )
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

    fun load(obj: Any?, options: RequestOptions?): ShapeImageView {
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
        init(context, attrs)
    }
}