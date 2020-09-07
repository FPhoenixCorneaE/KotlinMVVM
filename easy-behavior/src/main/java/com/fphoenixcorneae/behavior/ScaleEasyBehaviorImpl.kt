package com.fphoenixcorneae.behavior

import android.animation.ValueAnimator
import android.view.View

/**
 * ScaleEasyBehaviorImpl using for float button
 */
class ScaleEasyBehaviorImpl private constructor(var mTarget: View) : IEasyBehavior {
    override var state: Int = IEasyBehavior.STATE_SHOW
    override fun show() {
        val va = ValueAnimator.ofFloat(mTarget.scaleX, 1f)
        va.addUpdateListener { valueAnimator ->
            val scale = valueAnimator.animatedValue as Float
            mTarget.scaleX = scale
            mTarget.scaleY = scale
        }
        va.duration = 400
        va.start()
        state = IEasyBehavior.STATE_SHOW
    }

    override fun hide() {
        val va = ValueAnimator.ofFloat(mTarget.scaleX, 0f)
        va.addUpdateListener { valueAnimator ->
            val scale = valueAnimator.animatedValue as Float
            mTarget.scaleX = scale
            mTarget.scaleY = scale
        }
        va.duration = 400
        va.start()
        state = IEasyBehavior.STATE_HIDE
    }

    override fun setStartY(y: Float) {}
    override fun setMode(mode: Int) {}

    companion object {
        operator fun get(view: View): ScaleEasyBehaviorImpl {
            return ScaleEasyBehaviorImpl(view)
        }
    }

}