package com.wkz.framework.web

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.LinearLayout
import com.just.agentweb.AgentWeb
import com.just.agentweb.DefaultWebClient
import com.just.agentweb.NestedScrollAgentWebView
import com.wkz.framework.R
import com.wkz.framework.base.activity.BaseActivity
import com.wkz.titlebar.CommonTitleBar
import com.wkz.util.BundleBuilder
import com.wkz.util.DeviceIdUtil
import com.wkz.util.ImageUtil
import com.wkz.util.IntentUtil
import kotlinx.android.synthetic.main.framework_layout_base_web.*

/**
 * @desc:WebActivity基类
 * @date:2020-06-05 09:27
 */
open class BaseWebActivity : BaseActivity() {

    private lateinit var mAgentWeb: AgentWeb
    private var mUrl: String? = ""

    companion object {
        const val WEB_URL = "web_url"
        const val TITLE = "title"

        fun start(context: Context, title: String, webUrl: String) {
            IntentUtil.startActivity(
                context,
                BaseWebActivity::class.java,
                BundleBuilder.of()
                    .putString(TITLE, title)
                    .putString(WEB_URL, webUrl)
                    .get()
            )
        }
    }

    override fun getLayoutId(): Int = R.layout.framework_layout_base_web

    override fun initView() {
        mTbTitleBar.setBackgroundColor(getTitleBgColor())
        mTbTitleBar.centerTextView?.setTextColor(getCenterTextColor())
        mTbTitleBar.leftImageButton?.let {
            ImageUtil.setTintColor(it, getLeftImageColor())
        }
    }

    override fun initListener() {
        mTbTitleBar.setOnTitleBarClickListener(object : CommonTitleBar.OnTitleBarClickListener {
            override fun onClicked(v: View?, action: Int, extra: String?) {
                when (action) {
                    CommonTitleBar.MotionAction.ACTION_LEFT_BUTTON -> finish()
                }
            }
        })
    }

    override fun initData(savedInstanceState: Bundle?) {
        mTbTitleBar.centerTextView?.text = intent.getStringExtra(TITLE)
        mUrl = intent.getStringExtra(WEB_URL)

        // 加载网页
        mAgentWeb = AgentWeb.with(mContext)
            // 传入AgentWeb的父控件
            .setAgentWebParent(
                mLlWebContainer,
                1,
                LinearLayout.LayoutParams(-1, -1)
            )
            // 设置进度条
            .setCustomIndicator(CoolIndicatorLayout(mContext))
            // 设置IAgentWebSettings
            .setAgentWebWebSettings(CustomWebSettings())
            // WebViewClient ， 与 WebView 使用一致 ，但是请勿获取WebView调用setWebViewClient(xx)方法了,
            // 会覆盖AgentWeb DefaultWebClient,同时相应的中间件也会失效。
            .setWebViewClient(CustomWebViewClient())
            // WebChromeClient
            .setWebChromeClient(CustomWebChromeClient())
            // 权限拦截 2.0.0 加入
            .setPermissionInterceptor(CustomPermissionInterceptor())
            // 严格模式 Android 4.2.2 以下会放弃注入对象 ，使用AgentWebView没影响
            .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK)
            // 自定义UI  AgentWeb3.0.0 加入
            .setAgentWebUIController(CustomUIController(mContext))
            // 参数1是错误显示的布局，参数2点击刷新控件ID -1表示点击整个布局都刷新， AgentWeb 3.0.0 加入
            .setMainFrameErrorView(R.layout.agentweb_error_page, -1)
            // 设置WebChromeClient中间件，支持多个WebChromeClient，AgentWeb 3.0.0 加入
            .useMiddlewareWebChrome(CustomMiddlewareChromeClient())
            .additionalHttpHeader(getUrl(), "cookie", DeviceIdUtil.uniqueID)
            // 设置WebViewClient中间件，支持多个WebViewClient， AgentWeb 3.0.0 加入
            .useMiddlewareWebClient(CustomMiddlewareWebViewClient())
            // 打开其他页面时，弹窗质询用户前往其他应用 AgentWeb 3.0.0 加入
            .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.ASK)
            // 拦截找不到相关页面的Url AgentWeb 3.0.0 加入
            .interceptUnkownUrl()
            .setWebView(NestedScrollAgentWebView(mContext))
            // 创建AgentWeb
            .createAgentWeb()
            // 设置WebSettings
            .ready()
            // WebView载入该url地址的页面并显示
            .go(getUrl())
    }

    private fun getUrl(): String? = mUrl

    override fun isAlreadyLoadedData(): Boolean = true

    override fun onResume() {
        // 恢复
        mAgentWeb.webLifeCycle.onResume()
        super.onResume()
    }

    override fun onPause() {
        // 会暂停应用内所有WebView ，
        // 调用mWebView.resumeTimers();/mAgentWeb.getWebLifeCycle().onResume(); 恢复。
        mAgentWeb.webLifeCycle.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        mAgentWeb.webLifeCycle.onDestroy()
        super.onDestroy()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return when {
            mAgentWeb.handleKeyEvent(keyCode, event) -> {
                true
            }
            else -> super.onKeyDown(keyCode, event)
        }
    }

    open fun getTitleBgColor(): Int {
        return Color.WHITE
    }

    open fun getLeftImageColor(): Int {
        return Color.BLACK
    }

    open fun getCenterTextColor(): Int {
        return Color.BLACK
    }
}