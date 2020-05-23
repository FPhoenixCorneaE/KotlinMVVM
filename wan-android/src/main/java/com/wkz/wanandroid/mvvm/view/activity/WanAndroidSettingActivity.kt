package com.wkz.wanandroid.mvvm.view.activity

import android.os.Bundle
import android.view.View
import com.wkz.extension.androidViewModel
import com.wkz.extension.visible
import com.wkz.titlebar.CommonTitleBar
import com.wkz.wanandroid.R
import com.wkz.wanandroid.manager.WanAndroidUserManager
import com.wkz.wanandroid.mvvm.viewmodel.WanAndroidAccountViewModel
import kotlinx.android.synthetic.main.wan_android_activity_setting.*

/**
 * @desc: 设置Activity
 * @date: 2020-05-21 16:37
 */
class WanAndroidSettingActivity : WanAndroidBaseActivity() {

    /* 账号信息视图模型 */
    private val mAccountViewModel by androidViewModel<WanAndroidAccountViewModel>()

    override fun getLayoutId(): Int = R.layout.wan_android_activity_setting

    override fun initView() {
        // 设置标题栏主题样式
        setCommonTitleBarTheme(mTbTitleBar)
        mCvLogout.visible(WanAndroidUserManager.sHasLoggedOn)
    }

    override fun initListener() {
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