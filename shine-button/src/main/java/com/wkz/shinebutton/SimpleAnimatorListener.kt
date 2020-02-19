package com.wkz.shinebutton

import android.animation.Animator
import android.animation.Animator.AnimatorListener

open class SimpleAnimatorListener : AnimatorListener {
    override fun onAnimationStart(animator: Animator) {}
    override fun onAnimationEnd(animator: Animator) {}
    override fun onAnimationCancel(animator: Animator) {}
    override fun onAnimationRepeat(animator: Animator) {}
}