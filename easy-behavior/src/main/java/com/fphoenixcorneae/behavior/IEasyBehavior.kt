package com.fphoenixcorneae.behavior

/**
 * 行为动画接口
 */
interface IEasyBehavior {
    fun show()
    fun hide()
    fun setStartY(y: Float)
    fun setMode(mode: Int)
    val state: Int

    companion object {
        const val STATE_HIDE = 0
        const val STATE_SHOW = 1
    }
}