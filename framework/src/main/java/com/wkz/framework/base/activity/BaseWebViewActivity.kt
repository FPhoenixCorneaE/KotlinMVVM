package com.wkz.framework.base.activity

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.wkz.framework.R
import com.wkz.titlebar.CommonTitleBar
import com.wkz.util.ImageUtil
import com.ycbjie.webviewlib.InterWebListener
import com.ycbjie.webviewlib.X5WebUtils
import kotlinx.android.synthetic.main.framework_activity_base_web_view.*

/**
 * @desc:BaseWebViewActivity基类
 */
open class BaseWebViewActivity : AutoDisposeActivity() {

    private val mInterWebListener: InterWebListener = object : InterWebListener {
        override fun hindProgressBar() {
            mWpProgress.hide()
        }

        override fun showErrorView(@X5WebUtils.ErrorType type: Int) {
            when (type) {
                X5WebUtils.ErrorMode.NO_NET -> {
                    // 没有网络
                }
                X5WebUtils.ErrorMode.STATE_404 -> {
                    // 404，网页无法打开
                }
                X5WebUtils.ErrorMode.RECEIVED_ERROR -> {
                    // onReceivedError，请求网络出现error
                }
                X5WebUtils.ErrorMode.SSL_ERROR -> {
                    // 在加载资源时通知主机应用程序发生SSL错误
                }
                else -> {
                }
            }
        }

        override fun startProgress(newProgress: Int) {
            mWpProgress.setWebProgress(newProgress)
        }

        override fun showTitle(title: String) {
        }
    }

    companion object {
        const val WEB_URL = "web_url"
        const val TITLE = "title"
    }

    override fun getLayoutId(): Int = R.layout.framework_activity_base_web_view

    override fun initView() {
        mTbTitleBar.setBackgroundColor(getTitleBgColor())
        mTbTitleBar.leftImageButton?.let {
            ImageUtil.setTintColor(it, getLeftImageColor())
        }
        mWpProgress.show()
        when (getProgressColor().size) {
            1 -> mWpProgress.setColor(getProgressColor()[0])
            2 -> mWpProgress.setColor(getProgressColor()[0], getProgressColor()[1])
        }
    }

    override fun initListener() {
        mTbTitleBar.setListener(object : CommonTitleBar.OnTitleBarClickListener {
            override fun onClicked(v: View?, action: Int, extra: String?) {
                when (action) {
                    CommonTitleBar.MotionAction.ACTION_LEFT_BUTTON -> finish()
                }
            }
        })
        mWvX5Web.x5WebChromeClient.setWebListener(mInterWebListener)
        mWvX5Web.x5WebViewClient.setWebListener(mInterWebListener)
    }

    override fun initData(savedInstanceState: Bundle?) {
        mTbTitleBar.centerTextView?.text = intent.getStringExtra(TITLE)
        mWvX5Web.loadUrl(intent.getStringExtra(WEB_URL))
    }

    override fun isAlreadyLoadedData(): Boolean = true

    @SuppressLint("SetJavaScriptEnabled")
    override fun onResume() {
        super.onResume()
        mWvX5Web.settings.javaScriptEnabled = true
    }

    override fun onStop() {
        super.onStop()
        mWvX5Web.settings.javaScriptEnabled = false
    }

    override fun onDestroy() {
        mWvX5Web?.apply {
            clearHistory()
            val parent = parent as ViewGroup
            parent.removeView(this)
            destroy()
        }
        super.onDestroy()
    }

    override fun onBackPressed() {
        if (mWvX5Web.canGoBack()) {
            mWvX5Web.goBack()
            return
        }
        super.onBackPressed()
    }

    open fun getTitleBgColor(): Int {
        return Color.WHITE
    }

    open fun getLeftImageColor(): Int {
        return Color.GRAY
    }

    open fun getProgressColor(): IntArray {
        return arrayOf(Color.RED, Color.MAGENTA).toIntArray()
    }
}