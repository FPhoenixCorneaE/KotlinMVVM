package com.wkz.wanandroid.mvvm.view.fragment.web

import com.wkz.framework.web.BaseWebFragment
import com.wkz.util.ResourceUtil
import com.wkz.wanandroid.R

class WanAndroidWebFragment : BaseWebFragment() {

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