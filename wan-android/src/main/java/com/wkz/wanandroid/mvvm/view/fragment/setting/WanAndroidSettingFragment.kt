package com.wkz.wanandroid.mvvm.view.fragment.setting

import com.wkz.extension.androidViewModel
import com.wkz.extension.navigateUp
import com.wkz.extension.visible
import com.wkz.wanandroid.R
import com.wkz.wanandroid.manager.WanAndroidUserManager
import com.wkz.wanandroid.mvvm.view.fragment.WanAndroidBaseFragment
import com.wkz.wanandroid.mvvm.viewmodel.WanAndroidAccountViewModel
import kotlinx.android.synthetic.main.wan_android_fragment_setting.*

/**
 * @desc: 设置Fragment
 * @date: 2020-06-04 16:20
 */
class WanAndroidSettingFragment : WanAndroidBaseFragment() {

    /* 账号信息视图模型 */
    private val mAccountViewModel by androidViewModel<WanAndroidAccountViewModel>()

    override fun getLayoutId(): Int = R.layout.wan_android_fragment_setting

    override fun initView() {

    }

    override fun lazyLoadData() {
        // 设置标题栏主题样式
        setCommonTitleBarTheme(mTbTitleBar)
        mCvLogout.visible(WanAndroidUserManager.sHasLoggedOn)
    }

    override fun isAlreadyLoadedData(): Boolean = true

    override fun initListener() {
        mBtnLogout.setOnClickListener {
            // 退出登录
            mAccountViewModel.mLoginSuccess.postValue(false)
            WanAndroidUserManager.logout()

            navigateUp()
        }
    }
}