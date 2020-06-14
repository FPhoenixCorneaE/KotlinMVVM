package com.fphoenixcorneae.animated_bottom_view

import android.animation.ObjectAnimator
import android.view.View

/**
 *  放大缩小动画
 */
class RotateAnimationManager : AnimationInterface {

    var view: View? = null
    private var animator: ObjectAnimator? = null
    private var onAnimationUpdate: AnimationUpdate? = null


    override fun startAnimation() {
        view?.let {
            if (animator == null) {
                animator = ObjectAnimator.ofFloat(it, "rotation", 360.0f)
            }

            animator?.duration = 333L
            animator?.start()
            animator?.addUpdateListener { animation ->
                onAnimationUpdate?.onAnimationUpdate(animation.animatedFraction)
            }
        }
    }

    override fun stopAnimation() {
        view?.let {
            cancelAnimation()
        }
    }

    override fun cancelAnimation() {
        view?.let {
            animator?.cancel()
            it.scaleX = 1.0f
            it.scaleY = 1.0f
        }
    }

    override fun setAnimationUpdate(animationUpdate: AnimationUpdate) {
        this.onAnimationUpdate = animationUpdate
    }
}