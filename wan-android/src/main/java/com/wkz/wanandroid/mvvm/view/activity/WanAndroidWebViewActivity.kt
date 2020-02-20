package com.wkz.wanandroid.mvvm.view.activity

import android.graphics.Color
import com.wkz.framework.base.activity.BaseWebViewActivity
import com.wkz.util.ResourceUtil
import com.wkz.wanandroid.R

class WanAndroidWebViewActivity : BaseWebViewActivity() {

    override fun getTitleBgColor(): Int {
        return ResourceUtil.getColor(R.color.wan_android_colorPrimary)
    }

    override fun getLeftImageColor(): Int {
        return Color.BLACK
    }

    override fun getProgressColor(): IntArray {
        return arrayOf(Color.RED, Color.MAGENTA).toIntArray()
    }
}