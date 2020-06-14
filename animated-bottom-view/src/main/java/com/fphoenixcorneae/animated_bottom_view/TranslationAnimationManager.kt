package com.fphoenixcorneae.animated_bottom_view

import android.animation.ValueAnimator
import android.view.View
import android.view.animation.LinearInterpolator
import kotlin.math.cos

/**
 * 位移的动画
 */
class TranslationAnimationManager(
    private val startTranslationX: Float,
    private val endTranslationX: Float,
    private val startTranslationY: Float,
    private val maxTranslationY: Float
) : AnimationInterface {
    var view: View? = null
    private var animator: ValueAnimator? = null
    private var onAnimationUpdate: AnimationUpdate? = null


    override fun startAnimation() {
        view?.let {
            if (animator == null) {
                animator = ValueAnimator.ofFloat(0.0f, 1.0f)
            }

            animator?.duration = 333L
            animator?.start()
            animator?.interpolator = LinearInterpolator()
            animator?.addUpdateListener { animation ->
                onAnimationUpdate?.onAnimationUpdate(animation.animatedFraction)
                val t = animation.animatedFraction
                //x方向为三角函数曲线的运动轨迹
                val translationX =
                    (-0.5f * cos(t * Math.PI).toFloat() + 0.5f) * (endTranslationX - startTranslationX) + startTranslationX

                val v0 = t * 2.0f - 1.0f
                //y方向为抛物线的运动轨迹
                val translationY = (-v0 * v0 + 1.0f) * (maxTranslationY - 0)

                it.translationX = translationX
                it.translationY = translationY
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
        }
    }

    override fun setAnimationUpdate(animationUpdate: AnimationUpdate) {
        this.onAnimationUpdate = animationUpdate
    }
}