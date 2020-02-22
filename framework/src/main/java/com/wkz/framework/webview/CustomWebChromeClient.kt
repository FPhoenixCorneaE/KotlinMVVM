package com.wkz.framework.webview

import android.webkit.WebView
import com.just.agentweb.WebChromeClient
import com.orhanobut.logger.Logger

class CustomWebChromeClient : WebChromeClient() {
    override fun onProgressChanged(view: WebView, newProgress: Int) {
        super.onProgressChanged(view, newProgress)
        Logger.i("onProgressChanged:$newProgress  view:$view")
    }
}