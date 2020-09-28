package com.fphoenixcorneae.palette

import android.content.Context
import android.graphics.*
import android.os.AsyncTask
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.view.View
import androidx.palette.graphics.Palette
import androidx.palette.graphics.Palette.PaletteAsyncListener
import java.lang.ref.WeakReference
import kotlin.math.max
import kotlin.math.min

/**
 * 调色板图片视图
 */
class PaletteImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var mPaintShadow: Paint? = null
    private var mPaint: Paint? = null
    private var mRadius = 0
    private var mPadding = 0
    private var mBitmap: Bitmap? = null
    private var mImgId = 0
    private var mAsyncTask: AsyncTask<*, *, *>? = null
    var mMainColor = -1
    private var mOffsetX = DEFAULT_OFFSET
    private var mOffsetY = DEFAULT_OFFSET
    private var mShadowRadius = DEFAULT_SHADOW_RADIUS
    private var mPalette: Palette? = null
    private var mRectFShadow: RectF? = null
    private var mRealBitmap: Bitmap? = null
    private var mOnMeasureHeightMode = MeasureSpec.UNSPECIFIED
    var mInstance: PaletteImageView? = null
    private var mRoundBitmap: Bitmap? = null
    private var mRoundRectF: RectF? = null
    private var mPorterDuffXfermode: PorterDuffXfermode? = null
    private var mListener: OnParseColorListener? = null
    private var mHandler: Handler? = null
    private fun init(
        context: Context,
        attrs: AttributeSet?
    ) {
        mInstance = this
        val a = context.obtainStyledAttributes(attrs, R.styleable.PaletteImageView)
        mRadius = a.getDimensionPixelSize(R.styleable.PaletteImageView_piv_paletteRadius, 0)
        mImgId = a.getResourceId(R.styleable.PaletteImageView_piv_paletteSrc, 0)
        mPadding = a.getDimensionPixelSize(
            R.styleable.PaletteImageView_piv_palettePadding,
            DEFAULT_PADDING
        )
        mOffsetX = a.getDimensionPixelSize(
            R.styleable.PaletteImageView_piv_paletteOffsetX,
            DEFAULT_OFFSET
        )
        mOffsetY = a.getDimensionPixelSize(
            R.styleable.PaletteImageView_piv_paletteOffsetY,
            DEFAULT_OFFSET
        )
        mShadowRadius = a.getDimensionPixelSize(
            R.styleable.PaletteImageView_piv_paletteShadowRadius,
            DEFAULT_SHADOW_RADIUS
        )
        a.recycle()
        setPadding(mPadding, mPadding, mPadding, mPadding)
        mPaintShadow = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaintShadow!!.isDither = true
        setLayerType(LAYER_TYPE_SOFTWARE, null)
        setBackgroundColor(Color.TRANSPARENT)
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaint!!.isDither = true
        mPorterDuffXfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        mHandler = MyHandler(this)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        var height = MeasureSpec.getSize(heightMeasureSpec)
        mOnMeasureHeightMode = MeasureSpec.getMode(heightMeasureSpec)
        if (mOnMeasureHeightMode == MeasureSpec.UNSPECIFIED) {
            if (mBitmap != null) {
                height =
                    ((width - mPadding * 2) * (mBitmap!!.height * 1.0f / mBitmap!!.width)).toInt() + mPadding * 2
            }
            if (mImgId != 0 && mRealBitmap != null) {
                height = mRealBitmap!!.height + mPadding * 2
            }
        }
        if (mBitmap != null) {
            height =
                ((width - mPadding * 2) * (mBitmap!!.height * 1.0f / mBitmap!!.width)).toInt() + mPadding * 2
        }
        setMeasuredDimension(width, height)
    }

    override fun onSizeChanged(w: Int, h: Int, oldW: Int, oldH: Int) {
        super.onSizeChanged(w, h, oldW, oldH)
        zipBitmap(mImgId, mBitmap, mOnMeasureHeightMode)
        mRectFShadow = RectF(
            mPadding.toFloat(),
            mPadding.toFloat(),
            (width - mPadding).toFloat(),
            (height - mPadding).toFloat()
        )
        mRoundRectF =
            RectF(0f, 0f, (width - mPadding * 2).toFloat(), (height - mPadding * 2).toFloat())
        mRoundBitmap = createRoundCornerImage(mRealBitmap, mRadius)
    }

    override fun onDraw(canvas: Canvas) {
        if (mRealBitmap != null) {
            canvas.drawRoundRect(
                mRectFShadow!!,
                mRadius.toFloat(),
                mRadius.toFloat(),
                mPaintShadow!!
            )
            if (mRoundBitmap != null) {
                canvas.drawBitmap(mRoundBitmap!!, mPadding.toFloat(), mPadding.toFloat(), null)
            }
            if (mMainColor != -1) {
                mAsyncTask!!.cancel(true)
            }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mHandler!!.removeCallbacksAndMessages(null)
    }

    fun setShadowColor(color: Int) {
        mMainColor = color
        mHandler!!.sendEmptyMessage(MSG)
    }

    fun setBitmap(bitmap: Bitmap?) {
        mBitmap = bitmap
        zipBitmap(mImgId, mBitmap, mOnMeasureHeightMode)
    }

    fun setPaletteRadius(radius: Int) {
        mRadius = radius
        mRoundBitmap = createRoundCornerImage(mRealBitmap, mRadius)
        invalidate()
    }

    fun setPaletteShadowOffset(offsetX: Int, offsetY: Int) {
        mOffsetX = min(offsetX, mPadding)
        mOffsetY = min(offsetY, mPadding)
        mHandler!!.sendEmptyMessage(MSG)
    }

    fun setPaletteShadowRadius(radius: Int) {
        mShadowRadius = radius
        mHandler!!.sendEmptyMessage(MSG)
    }

    fun setOnParseColorListener(listener: OnParseColorListener?) {
        mListener = listener
    }

    private fun initShadow(bitmap: Bitmap?) {
        if (bitmap != null) {
            mAsyncTask = Palette.from(bitmap).generate(paletteAsyncListener)
        }
    }

    private fun createRoundCornerImage(
        source: Bitmap?,
        radius: Int
    ): Bitmap? {
        if (source == null) {
            return null
        }
        val target = Bitmap.createBitmap(
            width - mPadding * 2,
            height - mPadding * 2,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(target)
        canvas.drawRoundRect(mRoundRectF!!, radius.toFloat(), radius.toFloat(), mPaint!!)
        mPaint!!.xfermode = mPorterDuffXfermode
        canvas.drawBitmap(source, 0f, 0f, mPaint)
        mPaint!!.xfermode = null
        return target
    }

    private fun zipBitmap(
        imgId: Int,
        bitmap: Bitmap?,
        heightNode: Int
    ) {
        var bitmap = bitmap
        val weakMatrix =
            WeakReference(Matrix())
        if (weakMatrix.get() == null) {
            return
        }
        val matrix = weakMatrix.get()
        val reqWidth = width - mPadding - mPadding
        val reqHeight = height - mPadding - mPadding
        if (reqHeight <= 0 || reqWidth <= 0) {
            return
        }
        var rawWidth = 0
        var rawHeight = 0
        if (imgId != 0 && bitmap == null) {
            val weakOptions =
                WeakReference(BitmapFactory.Options())
            if (weakOptions.get() == null) {
                return
            }
            val options = weakOptions.get()
            BitmapFactory.decodeResource(resources, imgId, options)
            options!!.inJustDecodeBounds = true
            rawWidth = options.outWidth
            rawHeight = options.outHeight
            options.inSampleSize = calculateInSampleSize(
                rawWidth,
                rawHeight,
                width - mPadding * 2,
                height - mPadding * 2
            )
            options.inJustDecodeBounds = false
            bitmap = BitmapFactory.decodeResource(resources, mImgId, options)
        } else if (imgId == 0 && bitmap != null) {
            rawWidth = bitmap.width
            rawHeight = bitmap.height
            val scale = rawHeight * 1.0f / rawWidth
            mRealBitmap = Bitmap.createScaledBitmap(
                bitmap,
                reqWidth,
                (reqWidth * scale).toInt(),
                true
            )
            initShadow(mRealBitmap)
            return
        }
        if (heightNode == 0) {
            val scale = rawHeight * 1.0f / rawWidth
            mRealBitmap = Bitmap.createScaledBitmap(
                bitmap!!,
                reqWidth,
                (reqWidth * scale).toInt(),
                true
            )
        } else {
            var dx = 0
            var dy = 0
            val small = min(rawHeight, rawWidth)
            val big = max(reqWidth, reqHeight)
            val scale = big * 1.0f / small
            matrix!!.setScale(scale, scale)
            if (rawHeight > rawWidth) {
                dy = (rawHeight - rawWidth) / 2
            } else if (rawHeight < rawWidth) {
                dx = (rawWidth - rawHeight) / 2
            }
            if (small <= 0) {
                return
            }
            mRealBitmap =
                Bitmap.createBitmap(bitmap!!, dx, dy, small, small, matrix, true)
        }
        initShadow(mRealBitmap)
    }

    private fun calculateInSampleSize(
        rawWidth: Int,
        rawHeight: Int,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        var inSampleSize = 1
        if (rawHeight > reqHeight || rawWidth > reqWidth) {
            val halfHeight = rawHeight / 2
            val halfWidth = rawWidth / 2
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    private val paletteAsyncListener = PaletteAsyncListener { palette ->
        if (palette != null) {
            mPalette = palette
            mMainColor = palette.dominantSwatch?.rgb?:-1
            mHandler!!.sendEmptyMessage(MSG)
            if (mListener != null) {
                mListener!!.onComplete(mInstance)
            }
        } else {
            if (mListener != null) {
                mListener!!.onFail()
            }
        }
    }

    val vibrantColor: IntArray?
        get() {
            if (mPalette == null || mPalette!!.vibrantSwatch == null) {
                return null
            }
            val array = IntArray(3)
            array[0] = mPalette!!.vibrantSwatch!!.titleTextColor
            array[1] = mPalette!!.vibrantSwatch!!.bodyTextColor
            array[2] = mPalette!!.vibrantSwatch!!.rgb
            return array
        }

    val darkVibrantColor: IntArray?
        get() {
            if (mPalette == null || mPalette!!.darkVibrantSwatch == null) {
                return null
            }
            val array = IntArray(3)
            array[0] = mPalette!!.darkVibrantSwatch!!.titleTextColor
            array[1] = mPalette!!.darkVibrantSwatch!!.bodyTextColor
            array[2] = mPalette!!.darkVibrantSwatch!!.rgb
            return array
        }

    val lightVibrantColor: IntArray?
        get() {
            if (mPalette == null || mPalette!!.lightVibrantSwatch == null) {
                return null
            }
            val array = IntArray(3)
            array[0] = mPalette!!.lightVibrantSwatch!!.titleTextColor
            array[1] = mPalette!!.lightVibrantSwatch!!.bodyTextColor
            array[2] = mPalette!!.lightVibrantSwatch!!.rgb
            return array
        }

    val mutedColor: IntArray?
        get() {
            if (mPalette == null || mPalette!!.mutedSwatch == null) {
                return null
            }
            val array = IntArray(3)
            array[0] = mPalette!!.mutedSwatch!!.titleTextColor
            array[1] = mPalette!!.mutedSwatch!!.bodyTextColor
            array[2] = mPalette!!.mutedSwatch!!.rgb
            return array
        }

    val darkMutedColor: IntArray?
        get() {
            if (mPalette == null || mPalette!!.darkMutedSwatch == null) {
                return null
            }
            val array = IntArray(3)
            array[0] = mPalette!!.darkMutedSwatch!!.titleTextColor
            array[1] = mPalette!!.darkMutedSwatch!!.bodyTextColor
            array[2] = mPalette!!.darkMutedSwatch!!.rgb
            return array
        }

    val lightMutedColor: IntArray?
        get() {
            if (mPalette == null || mPalette!!.lightMutedSwatch == null) {
                return null
            }
            val array = IntArray(3)
            array[0] = mPalette!!.lightMutedSwatch!!.titleTextColor
            array[1] = mPalette!!.lightMutedSwatch!!.bodyTextColor
            array[2] = mPalette!!.lightMutedSwatch!!.rgb
            return array
        }

    private class MyHandler(paletteImageView: PaletteImageView?) :
        Handler() {
        private val mPaletteImageViewWeakReference: WeakReference<PaletteImageView?> = WeakReference(paletteImageView)
        override fun handleMessage(msg: Message) {
            if (mPaletteImageViewWeakReference.get() != null) {
                val paletteImageView = mPaletteImageViewWeakReference.get()
                if (paletteImageView!!.mOffsetX < DEFAULT_OFFSET) {
                    paletteImageView.mOffsetX = DEFAULT_OFFSET
                }
                if (paletteImageView.mOffsetY < DEFAULT_OFFSET) {
                    paletteImageView.mOffsetY = DEFAULT_OFFSET
                }
                if (paletteImageView.mShadowRadius < DEFAULT_SHADOW_RADIUS) {
                    paletteImageView.mShadowRadius =
                        DEFAULT_SHADOW_RADIUS
                }
                paletteImageView.mPaintShadow!!.setShadowLayer(
                    paletteImageView.mShadowRadius.toFloat(),
                    paletteImageView.mOffsetX.toFloat(),
                    paletteImageView.mOffsetY.toFloat(),
                    paletteImageView.mMainColor
                )
                paletteImageView.invalidate()
            }
        }

    }

    companion object {
        private const val MSG = 0x101
        private const val DEFAULT_PADDING = 40
        private const val DEFAULT_OFFSET = 20
        private const val DEFAULT_SHADOW_RADIUS = 20
    }

    init {
        init(context, attrs)
    }
}