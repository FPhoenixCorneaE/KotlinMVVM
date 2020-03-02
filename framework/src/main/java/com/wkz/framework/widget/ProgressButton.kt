package com.wkz.framework.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.WindowManager
import android.view.animation.*
import android.view.animation.Animation.AnimationListener

class ProgressButton @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val ProgressButtonDuration = 200
    private val scaleAnimationDuration = 300
    private val rotateAnimationDuration = 400
    private var paintRectF: Paint? = null
    private var paintText: Paint? = null
    private var paintPro: Paint? = null
    private var mStrokeWidth = 0
    private var mPadding = 0
    private var mSpac = 0f
    private var mRadius = 0f
    private var mProRadius = 0
    private val mStartAngle = 0f
    private var mProgressButtonAnim: ProgressButtonAnim? = null
    private var mProgressScaleAnim: ScaleAnimation? = null
    private var mProgressRotateAnim: RotateAnimation? = null
    private var text = ""
    private var bgColor = Color.RED
    private var textColor = Color.WHITE
    private var mTextSize = dip2px(15f).toFloat()
    private var proColor = Color.WHITE
    private var mStop = false
    fun setBgColor(color: Int): ProgressButton {
        bgColor = color
        return this
    }

    fun setTextColor(color: Int): ProgressButton {
        textColor = color
        return this
    }

    fun setTextSize(mTextSize: Float): ProgressButton {
        this.mTextSize = mTextSize
        return this
    }

    fun setProColor(color: Int): ProgressButton {
        proColor = color
        return this
    }

    fun setButtonText(s: String): ProgressButton {
        text = s
        invalidate()
        return this
    }

    private fun initPaint() {
        mStrokeWidth = dip2px(2f)
        mPadding = dip2px(2f)
        mProRadius = measuredHeight / 5
        mProgressButtonAnim = ProgressButtonAnim()
        mProgressRotateAnim = RotateAnimation(
            0f, 360f, Animation.RELATIVE_TO_SELF,
            0.5f, Animation.RELATIVE_TO_SELF, 0.5f
        )
        mProgressRotateAnim!!.repeatCount = -1
        // 不停顿
        mProgressRotateAnim!!.interpolator = LinearInterpolator()
        // 停在最后
        mProgressRotateAnim!!.fillAfter = true
        paintRectF = Paint()
        paintRectF!!.isAntiAlias = true
        paintRectF!!.style = Paint.Style.FILL
        paintRectF!!.strokeWidth = mStrokeWidth.toFloat()
        paintText = Paint()
        paintText!!.isAntiAlias = true
        paintText!!.style = Paint.Style.FILL
        paintPro = Paint()
        paintPro!!.isAntiAlias = true
        paintPro!!.style = Paint.Style.STROKE
        paintPro!!.strokeWidth = (mStrokeWidth shr 1.toFloat().toInt()).toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paintText!!.color = textColor
        paintText!!.textSize = mTextSize
        paintRectF!!.color = bgColor
        paintPro!!.color = proColor
        // RectF对象
        val mRectF = RectF()
        mRectF.left = mPadding + mSpac
        mRectF.top = mPadding.toFloat()
        mRectF.right = measuredWidth - mPadding - mSpac
        mRectF.bottom = measuredHeight - mPadding.toFloat()
        mRadius = (measuredHeight - 2 * mPadding shr 1.toFloat().toInt()).toFloat()
        canvas.drawRoundRect(mRectF, mRadius, mRadius, paintRectF!!)
        if (mRectF.width() == mRectF.height() && !mStop) {
            isClickable = true
            val mRectFPro = RectF()
            mRectFPro.left = measuredWidth / 2.0f - mRectF.width() / 4
            mRectFPro.top = measuredHeight / 2.0f - mRectF.width() / 4
            mRectFPro.right = measuredWidth / 2.0f + mRectF.width() / 4
            mRectFPro.bottom = measuredHeight / 2.0f + mRectF.width() / 4
            canvas.drawArc(mRectFPro, mStartAngle, 100f, false, paintPro!!)
        }
        if (mSpac < (measuredWidth - measuredHeight) / 2.0f) {
            canvas.drawText(
                text,
                measuredWidth / 2.0f - getFontLength(paintText, text) / 2.0f,
                measuredHeight / 2.0f + getFontHeight(paintText, text) / 3.0f,
                paintText!!
            )
        }
    }

    fun startAnim() {
        mStop = false
        isClickable = false
        if (mProgressButtonAnim != null) {
            clearAnimation()
            mProgressButtonAnim!!.duration = ProgressButtonDuration.toLong()
            startAnimation(mProgressButtonAnim)
        }
    }

    fun startProAnim() {
        if (mProgressRotateAnim != null) {
            clearAnimation()
            mProgressRotateAnim!!.duration = rotateAnimationDuration.toLong()
            startAnimation(mProgressRotateAnim)
        }
    }

    fun stopAnim(mOnStopAnim: OnStopAnim) {
        clearAnimation()
        mStop = true
        invalidate()
        if (mProgressScaleAnim != null) {
            clearAnimation()
        } else {
            val wm = context
                .getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val width = wm.defaultDisplay.width
            mProgressScaleAnim = ScaleAnimation(
                1.0f,
                width / measuredHeight * 3.5f,
                1.0f,
                width / measuredHeight * 3.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f
            )
        }
        mProgressScaleAnim!!.duration = scaleAnimationDuration.toLong()
        startAnimation(mProgressScaleAnim)
        mProgressScaleAnim!!.setAnimationListener(object : AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                clearAnimation()
                mOnStopAnim.onStop()
                mSpac = 0f
                invalidate()
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
    }

    private inner class ProgressButtonAnim : Animation() {
        override fun applyTransformation(
            interpolatedTime: Float,
            t: Transformation
        ) {
            super.applyTransformation(interpolatedTime, t)
            mSpac = (measuredWidth - measuredHeight) / 2.0f * interpolatedTime
            invalidate()
            if (interpolatedTime == 1.0f) {
                startProAnim()
            }
        }
    }

    fun dip2px(dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    fun getFontLength(paint: Paint?, str: String): Float {
        val rect = Rect()
        paint!!.getTextBounds(str, 0, str.length, rect)
        return rect.width().toFloat()
    }

    fun getFontHeight(paint: Paint?, str: String): Float {
        val rect = Rect()
        paint!!.getTextBounds(str, 0, str.length, rect)
        return rect.height().toFloat()
    }

    interface OnStopAnim {
        fun onStop()
    }

    init {
        initPaint()
    }
}