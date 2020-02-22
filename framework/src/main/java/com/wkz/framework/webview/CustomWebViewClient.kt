package com.wkz.framework.webview

import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Build
import android.webkit.*
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import com.just.agentweb.WebViewClient
import com.orhanobut.logger.Logger
import com.wkz.util.gson.GsonUtil

/**
 * 注意，重写WebViewClient的方法,super.xxx()请务必正确调用， 如果没有调用super.xxx(),则无法执行DefaultWebClient的方法
 * 可能会影响到AgentWeb自带提供的功能,尽可能调用super.xxx()来完成洋葱模型
 */
class CustomWebViewClient : WebViewClient() {

    override fun onReceivedError(
        view: WebView,
        request: WebResourceRequest,
        error: WebResourceError
    ) {
        super.onReceivedError(view, request, error)
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    override fun shouldOverrideUrlLoading(
        view: WebView,
        request: WebResourceRequest
    ): Boolean {
        return super.shouldOverrideUrlLoading(view, request)
    }

    @Nullable
    override fun shouldInterceptRequest(
        view: WebView,
        request: WebResourceRequest
    ): WebResourceResponse? {
        return super.shouldInterceptRequest(view, request)
    }

    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        // 优酷想唤起自己应用播放该视频 ， 下面拦截地址返回 true  则会在应用内 H5 播放 ，
        // 禁止优酷唤起播放该视频， 如果返回 false ， DefaultWebClient  会根据intent 协议处理
        // 该地址 ， 首先匹配该应用存不存在 ，如果存在 ， 唤起该应用播放 ， 如果不存在 ，
        // 则跳到应用市场下载该应用 .
        return if (url.startsWith("intent://") && url.contains("com.youku.phone")) {
            true
        } else super.shouldOverrideUrlLoading(view, url)
    }

    override fun onPageStarted(
        view: WebView?,
        url: String?,
        favicon: Bitmap?
    ) {
        super.onPageStarted(view, url, favicon)
    }

    override fun onPageFinished(view: WebView, url: String) {
        super.onPageFinished(view, url)
    }

    /*错误页回调该方法 ， 如果重写了该方法， 上面传入了布局将不会显示 ， 交由开发者实现，注意参数对齐。*/
    /* public void onMainFrameError(AbsAgentWebUIController agentWebUIController, WebView view, int errorCode, String description, String failingUrl) {

            Log.i(TAG, "AgentWebFragment onMainFrameError");
            agentWebUIController.onMainFrameError(view,errorCode,description,failingUrl);

        }*/
    override fun onReceivedHttpError(
        view: WebView,
        request: WebResourceRequest,
        errorResponse: WebResourceResponse
    ) {
        super.onReceivedHttpError(view, request, errorResponse)
        Logger.i(
            "onReceivedHttpError:" + 3 + "  request:" + GsonUtil.toJson(request) +
                    "  errorResponse:" + GsonUtil.toJson(errorResponse)
        )
    }

    override fun onReceivedSslError(
        view: WebView,
        handler: SslErrorHandler,
        error: SslError
    ) {
        handler.proceed()
        super.onReceivedSslError(view, handler, error)
    }

    override fun onReceivedError(
        view: WebView,
        errorCode: Int,
        description: String,
        failingUrl: String
    ) {
        super.onReceivedError(view, errorCode, description, failingUrl)
        Logger.i("onReceivedError:$errorCode  description:$description  errorResponse:$failingUrl")
    }
}