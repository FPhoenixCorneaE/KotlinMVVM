package com.fphoenixcorneae.animated_bottom_view

import android.view.View

/**
 *  放大缩小动画
 */
class RotateAnimationManager : AnimationInterface {

    var view: View? = null
    private var onAnimationUpdate: AnimationUpdate? = null
    private var endRotation = 0f

    override fun startAnimation() {
        stopAnimation()
        view?.let {
            endRotation = it.rotation + 360f
            it.animate()
                .rotation(endRotation)
                .setDuration(333L)
                .setUpdateListener { animation ->
                    onAnimationUpdate?.onAnimationUpdate(animation.animatedFraction)
                }
                .withLayer()
                .start()
        }
    }

    override fun stopAnimation() {
        view?.let {
            cancelAnimation()
        }
    }

    override fun cancelAnimation() {
        view?.let {
            it.animate().cancel()
            it.scaleX = 1.0f
            it.scaleY = 1.0f
            it.rotation = endRotation
        }
    }

    override fun setAnimationUpdate(animationUpdate: AnimationUpdate) {
        this.onAnimationUpdate = animationUpdate
    }
}