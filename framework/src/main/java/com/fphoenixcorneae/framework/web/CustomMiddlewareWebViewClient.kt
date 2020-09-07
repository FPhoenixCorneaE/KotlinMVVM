package com.fphoenixcorneae.framework.web

import android.webkit.WebResourceRequest
import android.webkit.WebView
import com.just.agentweb.MiddlewareWebClientBase
import com.orhanobut.logger.Logger

/**
 * 方法的执行顺序，例如下面用了7个中间件一个 WebViewClient
 *
 * .useMiddlewareWebClient(getMiddlewareWebClient())  // 1
 * .useMiddlewareWebClient(getMiddlewareWebClient())  // 2
 * .useMiddlewareWebClient(getMiddlewareWebClient())  // 3
 * .useMiddlewareWebClient(getMiddlewareWebClient())  // 4
 * .useMiddlewareWebClient(getMiddlewareWebClient())  // 5
 * .useMiddlewareWebClient(getMiddlewareWebClient())  // 6
 * .useMiddlewareWebClient(getMiddlewareWebClient())  // 7
 *  DefaultWebClient                                  // 8
 *  .setWebViewClient(mWebViewClient)                 // 9
 *
 *
 * 典型的洋葱模型
 * 对象内部的方法执行顺序: 1->2->3->4->5->6->7->8->9->8->7->6->5->4->3->2->1
 *
 *
 * 中断中间件的执行， 删除super.methodName(...) 这行即可
 *
 */
class CustomMiddlewareWebViewClient : MiddlewareWebClientBase() {
    /**
     *
     * @param view
     * @param url
     * @return
     */
    override fun shouldOverrideUrlLoading(view: WebView?, url: String): Boolean {
        Logger.e(
            "MiddlewareWebClientBase#shouldOverrideUrlLoading url:$url"
        )
        return when {
            super.shouldOverrideUrlLoading(
                view,
                url
            ) -> {
                // 执行 DefaultWebClient#shouldOverrideUrlLoading
                true
            }
            else -> false
        }
    }

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest): Boolean {
        Logger.e(
            "MiddlewareWebClientBase#shouldOverrideUrlLoading request url:" + request.url.toString()
        )
        return super.shouldOverrideUrlLoading(view, request)
    }
}