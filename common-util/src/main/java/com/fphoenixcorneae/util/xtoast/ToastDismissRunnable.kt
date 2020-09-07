package com.fphoenixcorneae.util.xtoast

import java.lang.ref.WeakReference

/**
 * Toast 定时销毁任务
 */
internal class ToastDismissRunnable(toast: XToast?) :
    WeakReference<XToast?>(toast), Runnable {
    override fun run() {
        val toast = get()
        if (toast != null && toast.isShow) {
            toast.cancel()
        }
    }
}