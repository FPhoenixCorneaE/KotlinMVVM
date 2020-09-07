package com.fphoenixcorneae.wanandroid.mvvm.view.activity

import android.view.View
import com.fphoenixcorneae.framework.base.activity.BaseActivity
import com.fphoenixcorneae.titlebar.CommonTitleBar
import com.fphoenixcorneae.util.ImageUtil
import com.fphoenixcorneae.util.ResourceUtil
import com.fphoenixcorneae.wanandroid.R

/**
 * @desc: WanAndroidBaseActivity
 * @date: 2020-05-23 10:58
 */
abstract class WanAndroidBaseActivity : BaseActivity() {
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
            setOnTitleBarClickListener(object : CommonTitleBar.OnTitleBarClickListener {
                override fun onClicked(v: View?, action: Int, extra: String?) {
                    when (action) {
                        CommonTitleBar.MotionAction.ACTION_LEFT_BUTTON -> finish()
                    }
                }
            })
        }
    }
}