package com.fphoenixcorneae.animated_bottom_view

/**
 * 执行动画的接口
 */
interface AnimationInterface {
    fun startAnimation()
    fun stopAnimation()
    fun cancelAnimation()
    fun setAnimationUpdate(animationUpdate: AnimationUpdate)
}

interface AnimationUpdate {
    fun onAnimationUpdate(percent: Float)
}