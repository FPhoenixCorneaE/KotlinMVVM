package com.fphoenixcorneae.arclayout

import android.annotation.TargetApi
import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.FrameLayout
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.ViewCompat
import com.fphoenixcorneae.arclayout.ClipPathManagerImpl.ClipPathCreator

abstract class AbstractClipShapeLayout : FrameLayout {
    private val clipPaint =
        Paint(Paint.ANTI_ALIAS_FLAG)
    private val clipPath = Path()
    protected var pdMode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
    protected var mDrawable: Drawable? = null
    private val clipPathManager: ClipPathManager? = ClipPathManagerImpl()
    private var requiersShapeUpdate = true
    private var clipBitmap: Bitmap? = null
    val rectView = Path()

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    override fun setBackground(background: Drawable) {
        //disabled here, please set a background to to this view child
        //super.setBackground(background);
    }

    override fun setBackgroundResource(resId: Int) {
        //disabled here, please set a background to to this view child
        //super.setBackgroundResource(resId);
    }

    override fun setBackgroundColor(color: Int) {
        //disabled here, please set a background to to this view child
        //super.setBackgroundColor(color);
    }

    private fun init(
        context: Context,
        attrs: AttributeSet?
    ) {
        clipPaint.isAntiAlias = true
        isDrawingCacheEnabled = true
        setWillNotDraw(false)
        clipPaint.color = Color.BLUE
        clipPaint.style = Paint.Style.FILL
        clipPaint.strokeWidth = 1f
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1) {
            clipPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
            setLayerType(
                View.LAYER_TYPE_SOFTWARE,
                clipPaint
            ) //Only works for software layers
        } else {
            clipPaint.xfermode = pdMode
            setLayerType(
                View.LAYER_TYPE_SOFTWARE,
                null
            ) //Only works for software layers
        }
    }

    protected fun dpToPx(dp: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            Resources.getSystem().displayMetrics
        ).toInt()
    }

    override fun onLayout(
        changed: Boolean,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int
    ) {
        super.onLayout(changed, left, top, right, bottom)
        if (changed) {
            requiresShapeUpdate()
        }
    }

    private fun requiresBitmap(): Boolean {
        return isInEditMode || clipPathManager != null && clipPathManager.requiresBitmap() || mDrawable != null
    }

    fun setmDrawable(drawable: Drawable?) {
        this.mDrawable = drawable
        requiresShapeUpdate()
    }

    fun setmDrawable(redId: Int) {
        setmDrawable(AppCompatResources.getDrawable(context, redId))
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        if (requiersShapeUpdate) {
            calculateLayout(canvas.width, canvas.height)
            requiersShapeUpdate = false
        }
        if (requiresBitmap()) {
            clipPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
            canvas.drawBitmap(clipBitmap!!, 0f, 0f, clipPaint)
        } else {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1) {
                canvas.drawPath(clipPath, clipPaint)
            } else {
                canvas.drawPath(rectView, clipPaint)
            }
        }
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1) {
            setLayerType(View.LAYER_TYPE_HARDWARE, null)
        }
    }

    private fun calculateLayout(width: Int, height: Int) {
        rectView.reset()
        rectView.addRect(
            0f,
            0f,
            1f * getWidth(),
            1f * getHeight(),
            Path.Direction.CW
        )
        if (clipPathManager != null) {
            if (width > 0 && height > 0) {
                clipPathManager.setupClipLayout(width, height)
                clipPath.reset()
                clipPath.set(clipPathManager.createMask(width, height))
                if (requiresBitmap()) {
                    if (clipBitmap != null) {
                        clipBitmap!!.recycle()
                    }
                    clipBitmap = Bitmap.createBitmap(
                        width,
                        height,
                        Bitmap.Config.ARGB_8888
                    )
                    val canvas = Canvas(clipBitmap!!)
                    if (mDrawable != null) {
                        mDrawable!!.setBounds(0, 0, width, height)
                        mDrawable!!.draw(canvas)
                    } else {
                        canvas.drawPath(clipPath, clipPathManager.paint)
                    }
                }

                //invert the path for android P
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
                    val success =
                        rectView.op(clipPath, Path.Op.DIFFERENCE)
                }

                //this needs to be fixed for 25.4.0
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && ViewCompat.getElevation(
                        this
                    ) > 0f
                ) {
                    try {
                        outlineProvider = outlineProvider
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
        postInvalidate()
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun getOutlineProvider(): ViewOutlineProvider {
        return object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                if (clipPathManager != null) {
                    val shadowConvexPath =
                        clipPathManager.shadowConvexPath
                    if (shadowConvexPath != null) {
                        try {
                            outline.setConvexPath(shadowConvexPath)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    }

    fun setClipPathCreator(createClipPath: ClipPathCreator?) {
        (clipPathManager as ClipPathManagerImpl?)!!.setClipPathCreator(createClipPath)
        requiresShapeUpdate()
    }

    fun requiresShapeUpdate() {
        requiersShapeUpdate = true
        postInvalidate()
    }
}