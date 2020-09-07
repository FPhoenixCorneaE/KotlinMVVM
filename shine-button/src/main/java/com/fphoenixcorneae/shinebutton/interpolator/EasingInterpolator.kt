package com.fphoenixcorneae.shinebutton.interpolator

import android.view.animation.Interpolator

/**
 * The Easing class provides a collection of ease functions. It does not use the standard 4 param
 * ease signature. Instead it uses a single param which indicates the current linear ratio (0 to 1) of the tween.
 */
class EasingInterpolator(private val ease: Ease) : Interpolator {
    override fun getInterpolation(input: Float): Float {
        return EasingProvider[ease, input]
    }

}