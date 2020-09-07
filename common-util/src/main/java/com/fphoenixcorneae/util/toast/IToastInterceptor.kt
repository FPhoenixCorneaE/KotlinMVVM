package com.fphoenixcorneae.util.toast

import android.widget.Toast

/**
 * Toast 拦截器接口
 */
interface IToastInterceptor {
    /**
     * 根据显示的文本决定是否拦截该 Toast
     */
    fun intercept(toast: Toast?, text: CharSequence?): Boolean
}