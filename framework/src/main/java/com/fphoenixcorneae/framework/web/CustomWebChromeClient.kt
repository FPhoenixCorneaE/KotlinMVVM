package com.fphoenixcorneae.framework.web

import android.webkit.WebView
import com.fphoenixcorneae.ext.loggerI
import com.just.agentweb.WebChromeClient

class CustomWebChromeClient : WebChromeClient() {
    override fun onProgressChanged(view: WebView, newProgress: Int) {
        super.onProgressChanged(view, newProgress)
        loggerI("onProgressChanged:$newProgress  view:$view")
    }
}