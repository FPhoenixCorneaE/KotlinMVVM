package com.fphoenixcorneae.ext

import androidx.annotation.StringRes
import com.fphoenixcorneae.util.toast.ToastUtil

fun toast(content: CharSequence?) {
    ToastUtil.show(content)
}

fun toast(@StringRes id: Int) {
    ToastUtil.show(id)
}


