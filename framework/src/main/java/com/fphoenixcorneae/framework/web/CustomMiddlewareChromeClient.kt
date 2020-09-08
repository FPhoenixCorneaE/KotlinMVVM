package com.fphoenixcorneae.framework.web

import android.webkit.JsResult
import android.webkit.WebView
import com.fphoenixcorneae.ext.loggerI
import com.just.agentweb.MiddlewareWebChromeBase

/**
 * After agentweb 3.0.0  ， allow dev to custom self WebChromeClient's MiddleWare  .
 */
class CustomMiddlewareChromeClient : MiddlewareWebChromeBase() {
    override fun onJsAlert(
        view: WebView,
        url: String,
        message: String,
        result: JsResult
    ): Boolean {
        loggerI("onJsAlert:$url")
        return super.onJsAlert(view, url, message, result)
    }

    override fun onProgressChanged(view: WebView, newProgress: Int) {
        super.onProgressChanged(view, newProgress)
        loggerI("onProgressChanged:")
    }
}