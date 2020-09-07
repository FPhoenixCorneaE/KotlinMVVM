package com.fphoenixcorneae.util.toast

import android.widget.Toast

/**
 * Toast 处理策略
 */
interface IToastStrategy {
    /**
     * 绑定 Toast 对象
     */
    fun bind(toast: Toast?)

    /**
     * 显示 Toast
     */
    fun show(text: CharSequence?)

    /**
     * 取消 Toast
     */
    fun cancel()

    companion object {
        /**
         * 短吐司显示的时长
         */
        const val SHORT_DURATION_TIMEOUT = 2000
        /**
         * 长吐司显示的时长
         */
        const val LONG_DURATION_TIMEOUT = 3500
    }
}