package com.fphoenixcorneae.framework.web

import android.webkit.JsResult
import android.webkit.WebView
import com.just.agentweb.MiddlewareWebChromeBase
import com.orhanobut.logger.Logger

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
        Logger.i("onJsAlert:$url")
        return super.onJsAlert(view, url, message, result)
    }

    override fun onProgressChanged(view: WebView, newProgress: Int) {
        super.onProgressChanged(view, newProgress)
        Logger.i("onProgressChanged:")
    }
}