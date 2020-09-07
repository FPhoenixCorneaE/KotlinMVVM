package com.fphoenixcorneae.util.toast.style

import android.content.Context
import android.view.Gravity

/**
 * 支付宝样式实现
 */
class ToastAliPayStyle(context: Context) : BaseToastStyle(context) {
    override val gravity: Int
        get() = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM

    override val yOffset: Int
        get() = dp2px(100f)

    override val cornerRadius: Int
        get() = dp2px(5f)

    override val backgroundColor: Int
        get() = -0x11a8a8a9

    override val textColor: Int
        get() = -0x1

    override val textSize: Float
        get() = sp2px(16f).toFloat()

    override val paddingStart: Int
        get() = dp2px(16f)

    override val paddingTop: Int
        get() = dp2px(10f)
}