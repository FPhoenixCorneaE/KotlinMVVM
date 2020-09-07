package com.fphoenixcorneae.shinebutton

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.fphoenixcorneae.shinebutton.interpolator.Ease
import com.fphoenixcorneae.shinebutton.interpolator.EasingInterpolator
import java.util.*

class ShineView : View {
    var shineAnimator: ShineAnimator? = null
    var clickAnimator: ValueAnimator? = null
    var shineButton: ShineButton? = null
    private var paint: Paint? = null
    private var paint2: Paint? = null
    private var paintSmall: Paint? = null
    var colorCount = 10
    /**
     * Customer property
     */
    var shineCount = 0
    var smallOffsetAngle = 0f
    var turnAngle = 0f
    var animDuration: Long = 0
    var clickAnimDuration: Long = 0
    var shineDistanceMultiple = 0f
    var smallShineColor = colorRandom[0]
    var bigShineColor = colorRandom[1]
    var shineSize = 0
    var allowRandomColor = false
    var enableFlashing = false
    var rectF = RectF()
    var rectFSmall = RectF()
    var random = Random()
    var centerAnimX = 0
    var centerAnimY = 0
    var btnWidth = 0
    var btnHeight = 0
    var thirdLength = 0.0
    var value = 0f
    var clickValue = 0f
    var isRun = false
    private val distanceOffset = 0.2f

    constructor(context: Context?) : super(context)
    constructor(context: Context?, shineButton: ShineButton, shineParams: ShineParams) : super(
        context
    ) {
        initShineParams(shineParams, shineButton)
        shineAnimator = ShineAnimator(animDuration, shineDistanceMultiple, clickAnimDuration)
        ValueAnimator.setFrameDelay(FRAME_REFRESH_DELAY)
        this.shineButton = shineButton
        paint = Paint()
        paint!!.color = bigShineColor
        paint!!.strokeWidth = 20f
        paint!!.style = Paint.Style.STROKE
        paint!!.strokeCap = Paint.Cap.ROUND
        paint2 = Paint()
        paint2!!.color = Color.WHITE
        paint2!!.strokeWidth = 20f
        paint2!!.strokeCap = Paint.Cap.ROUND
        paintSmall = Paint()
        paintSmall!!.color = smallShineColor
        paintSmall!!.strokeWidth = 10f
        paintSmall!!.style = Paint.Style.STROKE
        paintSmall!!.strokeCap = Paint.Cap.ROUND
        clickAnimator = ValueAnimator.ofFloat(0f, 1.1f)
        ValueAnimator.setFrameDelay(FRAME_REFRESH_DELAY)
        clickAnimator!!.duration = clickAnimDuration
        clickAnimator!!.interpolator = EasingInterpolator(Ease.QUART_OUT)
        clickAnimator!!.addUpdateListener { valueAnimator: ValueAnimator ->
            clickValue = valueAnimator.animatedValue as Float
            invalidate()
        }
        clickAnimator!!.addListener(object : SimpleAnimatorListener() {
            override fun onAnimationEnd(animator: Animator) {
                clickValue = 0f
                invalidate()
            }
        })
        shineAnimator!!.addListener(object : SimpleAnimatorListener() {
            override fun onAnimationEnd(animator: Animator) {
                shineButton.removeView(this@ShineView)
            }
        })
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    fun showAnimation(shineButton: ShineButton?) {
        btnWidth = shineButton!!.width
        btnHeight = shineButton.height
        thirdLength = getThirdLength(btnHeight, btnWidth)
        val location = IntArray(2)
        shineButton.getLocationInWindow(location)
        centerAnimX = location[0] + shineButton.width / 2
        centerAnimY = location[1] + shineButton.height / 2
        if (shineButton.mFixDialog != null && shineButton.mFixDialog!!.window != null) {
            val decor = shineButton.mFixDialog!!.window?.decorView
            centerAnimX -= decor?.paddingLeft ?: 0
            centerAnimY -= decor?.paddingTop ?: 0
        }
        shineAnimator!!.addUpdateListener { valueAnimator: ValueAnimator ->
            value = valueAnimator.animatedValue as Float
            if (shineSize != 0 && shineSize > 0) {
                paint!!.strokeWidth = shineSize * (shineDistanceMultiple - value)
                paintSmall!!.strokeWidth =
                    shineSize.toFloat() / 3 * 2 * (shineDistanceMultiple - value)
            } else {
                paint!!.strokeWidth = btnWidth / 2 * (shineDistanceMultiple - value)
                paintSmall!!.strokeWidth = btnWidth / 3 * (shineDistanceMultiple - value)
            }
            rectF[centerAnimX - btnWidth / (3 - shineDistanceMultiple) * value, centerAnimY - btnHeight / (3 - shineDistanceMultiple) * value, centerAnimX + btnWidth / (3 - shineDistanceMultiple) * value] =
                centerAnimY + btnHeight / (3 - shineDistanceMultiple) * value
            rectFSmall[centerAnimX - btnWidth / (3 - shineDistanceMultiple + distanceOffset) * value, centerAnimY - btnHeight / (3 - shineDistanceMultiple + distanceOffset) * value, centerAnimX + btnWidth / (3 - shineDistanceMultiple + distanceOffset) * value] =
                centerAnimY + btnHeight / (3 - shineDistanceMultiple + distanceOffset) * value
            invalidate()
        }
        shineAnimator!!.startAnim()
        clickAnimator!!.start()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (i in 0 until shineCount) {
            if (allowRandomColor) {
                paint!!.color =
                    colorRandom[if (Math.abs(colorCount / 2 - i) >= colorCount) colorCount - 1 else Math.abs(
                        colorCount / 2 - i
                    )]
            }
            canvas.drawArc(
                rectF,
                360f / shineCount * i + 1 + (value - 1) * turnAngle,
                0.1f,
                false,
                getConfigPaint(paint!!)
            )
        }
        for (i in 0 until shineCount) {
            if (allowRandomColor) {
                paint!!.color =
                    colorRandom[if (Math.abs(colorCount / 2 - i) >= colorCount) colorCount - 1 else Math.abs(
                        colorCount / 2 - i
                    )]
            }
            canvas.drawArc(
                rectFSmall,
                360f / shineCount * i + 1 - smallOffsetAngle + (value - 1) * turnAngle,
                0.1f,
                false,
                getConfigPaint(paintSmall!!)
            )
        }
        paint!!.strokeWidth = btnWidth * clickValue * (shineDistanceMultiple - distanceOffset)
        if (clickValue != 0f) {
            paint2!!.strokeWidth =
                btnWidth * clickValue * (shineDistanceMultiple - distanceOffset) - 8
        } else {
            paint2!!.strokeWidth = 0f
        }
        canvas.drawPoint(centerAnimX.toFloat(), centerAnimY.toFloat(), paint!!)
        canvas.drawPoint(centerAnimX.toFloat(), centerAnimY.toFloat(), paint2!!)
        if (shineAnimator != null && !isRun) {
            isRun = true
            showAnimation(shineButton)
        }
    }

    private fun getConfigPaint(paint: Paint): Paint {
        if (enableFlashing) {
            paint.color = colorRandom[random.nextInt(colorCount - 1)]
        }
        return paint
    }

    private fun getThirdLength(btnHeight: Int, btnWidth: Int): Double {
        val all = btnHeight * btnHeight + btnWidth * btnWidth
        return Math.sqrt(all.toDouble())
    }

    class ShineParams internal constructor() {
        var allowRandomColor = false
        var animDuration: Long = 1500
        var bigShineColor = 0
        var clickAnimDuration: Long = 200
        var enableFlashing = false
        var shineCount = 7
        var shineTurnAngle = 20f
        var shineDistanceMultiple = 1.5f
        var smallShineOffsetAngle = 20f
        var smallShineColor = 0
        var shineSize = 0

        init {
            colorRandom[0] = Color.parseColor("#FFFF99")
            colorRandom[1] = Color.parseColor("#FFCCCC")
            colorRandom[2] = Color.parseColor("#996699")
            colorRandom[3] = Color.parseColor("#FF6666")
            colorRandom[4] = Color.parseColor("#FFFF66")
            colorRandom[5] = Color.parseColor("#F44336")
            colorRandom[6] = Color.parseColor("#666666")
            colorRandom[7] = Color.parseColor("#CCCC00")
            colorRandom[8] = Color.parseColor("#666666")
            colorRandom[9] = Color.parseColor("#999933")
        }
    }

    private fun initShineParams(shineParams: ShineParams, shineButton: ShineButton) {
        shineCount = shineParams.shineCount
        turnAngle = shineParams.shineTurnAngle
        smallOffsetAngle = shineParams.smallShineOffsetAngle
        enableFlashing = shineParams.enableFlashing
        allowRandomColor = shineParams.allowRandomColor
        shineDistanceMultiple = shineParams.shineDistanceMultiple
        animDuration = shineParams.animDuration
        clickAnimDuration = shineParams.clickAnimDuration
        smallShineColor = shineParams.smallShineColor
        bigShineColor = shineParams.bigShineColor
        shineSize = shineParams.shineSize
        if (smallShineColor == 0) {
            smallShineColor = colorRandom[6]
        }
        if (bigShineColor == 0) {
            bigShineColor = shineButton.color
        }
    }

    companion object {
        /**
         * default 10ms ,change to 25ms for saving cpu.
         */
        private const val FRAME_REFRESH_DELAY: Long = 25
        var colorRandom = IntArray(10)
    }
}