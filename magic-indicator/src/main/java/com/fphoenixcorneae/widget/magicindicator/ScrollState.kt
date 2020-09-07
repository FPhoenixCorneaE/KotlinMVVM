package com.fphoenixcorneae.widget.magicindicator

import androidx.annotation.Keep

/**
 * 自定义滚动状态，消除对ViewPager的依赖
 */
@Keep
interface ScrollState {
    companion object {
        const val SCROLL_STATE_IDLE = 0
        const val SCROLL_STATE_DRAGGING = 1
        const val SCROLL_STATE_SETTLING = 2
    }
}
