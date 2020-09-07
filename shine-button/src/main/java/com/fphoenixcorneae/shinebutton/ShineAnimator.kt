package com.fphoenixcorneae.shinebutton

import android.animation.ValueAnimator
import android.graphics.Canvas
import com.fphoenixcorneae.shinebutton.interpolator.Ease
import com.fphoenixcorneae.shinebutton.interpolator.EasingInterpolator

class ShineAnimator : ValueAnimator {
    private val MAX_VALUE = 1.5f
    private val ANIM_DURATION: Long = 1500
    private var canvas: Canvas? = null

    internal constructor() {
        setFloatValues(1f, MAX_VALUE)
        duration = ANIM_DURATION
        startDelay = 200
        interpolator = EasingInterpolator(Ease.QUART_OUT)
    }

    internal constructor(duration: Long, maxValue: Float, delay: Long) {
        setFloatValues(1f, maxValue)
        setDuration(duration)
        startDelay = delay
        interpolator = EasingInterpolator(Ease.QUART_OUT)
    }

    fun startAnim() {
        start()
    }

    fun setCanvas(canvas: Canvas?) {
        this.canvas = canvas
    }
}