package com.fphoenixcorneae.util.toast.style

import android.content.Context

/**
 * 默认黑色样式实现
 */
open class ToastBlackStyle(context: Context) : BaseToastStyle(context) {
    override val cornerRadius: Int
        get() = dp2px(8f)

    override val backgroundColor: Int
        get() = -0x78000000

    override val textColor: Int
        get() = -0x11000001

    override val textSize: Float
        get() = sp2px(14f).toFloat()

    override val paddingStart: Int
        get() = dp2px(24f)

    override val paddingTop: Int
        get() = dp2px(16f)
}