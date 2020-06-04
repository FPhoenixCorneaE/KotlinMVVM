package com.wkz.wanandroid.mvvm.view.fragment

import android.view.View
import com.wkz.extension.navigateUp
import com.wkz.framework.base.fragment.BaseFragment
import com.wkz.titlebar.CommonTitleBar
import com.wkz.util.ImageUtil
import com.wkz.util.ResourceUtil
import com.wkz.wanandroid.R

/**
 * @desc: WanAndroidBaseFragment
 * @date: 2020-06-04 16:20
 */
abstract class WanAndroidBaseFragment : BaseFragment() {
    /**
     * 设置标题栏主题样式
     */
    protected fun setCommonTitleBarTheme(commonTitleBar: CommonTitleBar?) {
        commonTitleBar?.apply {
            leftImageButton?.let {
                ImageUtil.setTintColor(
                    it,
                    ResourceUtil.getColor(R.color.wan_android_color_title_0x222222)
                )
            }
            setListener(object : CommonTitleBar.OnTitleBarClickListener {
                override fun onClicked(v: View?, action: Int, extra: String?) {
                    when (action) {
                        CommonTitleBar.MotionAction.ACTION_LEFT_BUTTON -> navigateUp()
                    }
                }
            })
        }
    }
}