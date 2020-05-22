package com.wkz.wanandroid.mvvm.view.activity

import android.content.Context
import com.wkz.framework.webview.BaseWebViewActivity
import com.wkz.util.BundleBuilder
import com.wkz.util.IntentUtil
import com.wkz.util.ResourceUtil
import com.wkz.wanandroid.R

class WanAndroidWebViewActivity : BaseWebViewActivity() {

    companion object {
        fun start(context: Context, title: String, webUrl: String) {
            IntentUtil.startActivity(
                context,
                WanAndroidWebViewActivity::class.java,
                BundleBuilder.of()
                    .putString(TITLE, title)
                    .putString(WEB_URL, webUrl)
                    .get()
            )
        }
    }

    override fun getTitleBgColor(): Int {
        return ResourceUtil.getColor(R.color.wan_android_colorPrimary)
    }

    override fun getLeftImageColor(): Int {
        return ResourceUtil.getColor(R.color.wan_android_color_title_0x222222)
    }

    override fun getCenterTextColor(): Int {
        return ResourceUtil.getColor(R.color.wan_android_color_title_0x222222)
    }
}