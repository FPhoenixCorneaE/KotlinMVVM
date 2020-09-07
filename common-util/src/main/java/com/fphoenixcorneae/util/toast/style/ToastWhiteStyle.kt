package com.fphoenixcorneae.util.toast.style

import android.content.Context

/**
 * 白色样式实现
 */
class ToastWhiteStyle(context: Context) : ToastBlackStyle(context) {
    override val backgroundColor: Int
        get() = -0x151516

    override val textColor: Int
        get() = -0x45000000
}