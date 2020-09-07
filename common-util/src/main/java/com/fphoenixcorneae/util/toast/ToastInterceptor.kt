package com.fphoenixcorneae.util.toast

import android.widget.Toast

/**
 * Toast 默认拦截器
 */
class ToastInterceptor : IToastInterceptor {
    override fun intercept(
        toast: Toast?,
        text: CharSequence?
    ): Boolean {
        // 如果是空对象或者空文本或者空字符串就进行拦截
        return text == null
                || text.toString().isBlank()
                || text.toString() == "null"
    }
}