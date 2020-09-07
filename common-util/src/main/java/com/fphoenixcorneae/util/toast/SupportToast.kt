package com.fphoenixcorneae.util.toast

import android.app.Application

/**
 * Toast 无通知栏权限兼容
 */
internal class SupportToast(application: Application) :
    BaseToast(application) {
    /**
     * 吐司弹窗显示辅助类
     */
    private val mToastHelper: ToastHelper = ToastHelper(this, application)

    override fun show() { // 显示吐司
        mToastHelper.show()
    }

    override fun cancel() { // 取消显示
        mToastHelper.cancel()
    }

}