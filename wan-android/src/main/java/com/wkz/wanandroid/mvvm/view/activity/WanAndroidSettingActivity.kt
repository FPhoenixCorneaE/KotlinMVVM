package com.wkz.wanandroid.mvvm.view.activity

import android.os.Bundle
import android.view.View
import com.wkz.extension.androidViewModel
import com.wkz.extension.visible
import com.wkz.framework.base.activity.BaseActivity
import com.wkz.titlebar.CommonTitleBar
import com.wkz.util.ImageUtil
import com.wkz.util.ResourceUtil
import com.wkz.wanandroid.R
import com.wkz.wanandroid.manager.WanAndroidUserManager
import com.wkz.wanandroid.mvvm.viewmodel.WanAndroidAccountViewModel
import kotlinx.android.synthetic.main.wan_android_activity_setting.*

/**
 * @desc: 设置Activity
 * @date: 2020-05-21 16:37
 */
class WanAndroidSettingActivity : BaseActivity() {

    /* 账号信息视图模型 */
    private val mAccountViewModel by androidViewModel<WanAndroidAccountViewModel>()

    override fun getLayoutId(): Int = R.layout.wan_android_activity_setting

    override fun initView() {
        mTbTitleBar.setBackgroundColor(ResourceUtil.getColor(R.color.wan_android_colorPrimary))
        mTbTitleBar.leftImageButton?.let {
            ImageUtil.setTintColor(
                it,
                ResourceUtil.getColor(R.color.wan_android_color_title_0x222222)
            )
        }
        mCvLogout.visible(WanAndroidUserManager.sHasLoggedOn)
    }

    override fun initListener() {
        mTbTitleBar.setListener(object : CommonTitleBar.OnTitleBarClickListener {
            override fun onClicked(v: View?, action: Int, extra: String?) {
                when (action) {
                    CommonTitleBar.MotionAction.ACTION_LEFT_BUTTON -> finish()
                }
            }
        })
        mBtnLogout.setOnClickListener {
            // 退出登录
            mAccountViewModel.mLoginSuccess.postValue(false)
            WanAndroidUserManager.logout()

            finish()
        }
    }

    override fun initData(savedInstanceState: Bundle?) {
    }
}