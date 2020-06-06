package com.wkz.framework.web

import android.app.Activity
import android.net.Uri
import android.os.Build
import android.webkit.DownloadListener
import android.webkit.WebView
import com.download.library.*
import com.just.agentweb.*

class CustomWebSettings : AbsAgentWebSettings() {

    private var agentWeb: AgentWeb? = null

    override fun bindAgentWebSupport(agentWeb: AgentWeb) {
        this.agentWeb = agentWeb
    }

    override fun toSetting(webView: WebView?): IAgentWebSettings<*>? {
        super.toSetting(webView)
        // 是否阻塞加载网络图片  协议http or https
        webSettings.blockNetworkImage = false
        // 允许加载本地文件html  file协议, 这可能会造成不安全 , 建议重写关闭
        webSettings.allowFileAccess = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            // 通过 file mUrl 加载的 Javascript 读取其他的本地文件 .建议关闭
            webSettings.allowFileAccessFromFileURLs = false
            // 允许通过 file mUrl 加载的 Javascript 可以访问其他的源，包括其他的文件和 http，https 等其他的源
            webSettings.allowUniversalAccessFromFileURLs = false
        }
        webSettings.setNeedInitialFocus(true)
        // 设置编码格式
        webSettings.defaultTextEncodingName = "utf-8"
        webSettings.defaultFontSize = 16
        // 设置 WebView 支持的最小字体大小，默认为 8
        webSettings.minimumFontSize = 12
        webSettings.setGeolocationEnabled(true)
        webSettings.userAgentString = webSettings.userAgentString + "agentweb/3.1.0"
        return this
    }

    /**
     * AgentWeb 4.0.0 内部删除了 DownloadListener 监听 ，以及相关API ，将 Download 部分完全抽离出来独立一个库，
     * 如果你需要使用 AgentWeb Download 部分 ， 请依赖上 compile 'com.download.library:Downloader:4.1.1' ，
     * 如果你需要监听下载结果，请自定义 AgentWebSetting ， New 出 DefaultDownloadImpl
     * 实现进度或者结果监听，例如下面这个例子，如果你不需要监听进度，或者下载结果，下面 setDownloader 的例子可以忽略。
     * @param webView
     * @param downloadListener
     * @return WebListenerManager
     */
    override fun setDownloader(
        webView: WebView,
        downloadListener: DownloadListener?
    ): WebListenerManager {
        return super.setDownloader(webView,
            object : DefaultDownloadImpl(
                webView.context as Activity,
                webView,
                this.agentWeb?.permissionInterceptor
            ) {
                override fun createResourceRequest(url: String): ResourceRequest<*>? {
                    return DownloadImpl.getInstance()
                        .with(webView.context.applicationContext)
                        .url(url)
                        .quickProgress()
                        .addHeader("", "")
                        .setEnableIndicator(true)
                        .autoOpenIgnoreMD5()
                        .setRetry(5)
                        .setBlockMaxTime(100000L)
                }

                override fun taskEnqueue(resourceRequest: ResourceRequest<*>) {
                    resourceRequest.enqueue(object : DownloadListenerAdapter() {
                        override fun onStart(
                            url: String,
                            userAgent: String,
                            contentDisposition: String,
                            mimetype: String,
                            contentLength: Long,
                            extra: Extra
                        ) {
                            super.onStart(
                                url,
                                userAgent,
                                contentDisposition,
                                mimetype,
                                contentLength,
                                extra
                            )
                        }

                        @DownloadingListener.MainThread
                        override fun onProgress(
                            url: String,
                            downloaded: Long,
                            length: Long,
                            usedTime: Long
                        ) {
                            super.onProgress(url, downloaded, length, usedTime)
                        }

                        override fun onResult(
                            throwable: Throwable,
                            path: Uri,
                            url: String,
                            extra: Extra
                        ): Boolean {
                            return super.onResult(throwable, path, url, extra)
                        }
                    })
                }
            })
    }
}