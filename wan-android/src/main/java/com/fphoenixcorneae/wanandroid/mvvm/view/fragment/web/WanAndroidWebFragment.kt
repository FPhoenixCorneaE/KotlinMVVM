package com.fphoenixcorneae.wanandroid.mvvm.view.fragment.web

import com.fphoenixcorneae.framework.web.BaseWebFragment
import com.fphoenixcorneae.util.ImageUtil
import com.fphoenixcorneae.util.ResourceUtil
import com.fphoenixcorneae.wanandroid.R
import kotlinx.android.synthetic.main.wan_android_fragment_web.*

class WanAndroidWebFragment : BaseWebFragment() {

    override fun getLayoutId(): Int = R.layout.wan_android_fragment_web

    override fun initView() {
        mTbTitleBar.setBackgroundColor(getTitleBgColor())
        mTbTitleBar.setStatusBarColor(getStatusBarColor())
        mTbTitleBar.centerTextView?.setTextColor(getCenterTextColor())
        mTbTitleBar.leftImageButton?.let {
            ImageUtil.setTintColor(it, getLeftImageColor())
        }
    }

    override fun getTitleBgColor(): Int {
        return ResourceUtil.getColor(R.color.wan_android_colorPrimary)
    }

    override fun getStatusBarColor(): Int {
        return ResourceUtil.getColor(R.color.wan_android_colorPrimaryDark)
    }

    override fun getLeftImageColor(): Int {
        return ResourceUtil.getColor(R.color.wan_android_color_title_0x222222)
    }

    override fun getCenterTextColor(): Int {
        return ResourceUtil.getColor(R.color.wan_android_color_title_0x222222)
    }
}