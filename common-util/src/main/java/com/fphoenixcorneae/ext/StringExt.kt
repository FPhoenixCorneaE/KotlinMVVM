package com.fphoenixcorneae.ext

import android.annotation.SuppressLint
import android.os.Build
import android.text.Html
import android.text.Spanned

fun String.toHtml(@SuppressLint("InlinedApi") flag: Int = Html.FROM_HTML_MODE_LEGACY): Spanned {
    return when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> {
            Html.fromHtml(this, flag)
        }
        else -> {
            Html.fromHtml(this)
        }
    }
}