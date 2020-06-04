package com.wkz.wanandroid.mvvm.view.activity

import android.os.Bundle
import com.wkz.extension.navigateUp
import com.wkz.wanandroid.R

/**
 * @desc: 首页Activity
 * @date: 2019-10-28 16:04
 */
class WanAndroidMainActivity : WanAndroidBaseActivity() {

    /**
     *  加载布局
     */
    override fun getLayoutId(): Int = R.layout.wan_android_activity_main

    /**
     * 初始化 View
     */
    override fun initView() {

    }

    override fun initListener() {

    }

    /**
     * 初始化数据
     */
    override fun initData(savedInstanceState: Bundle?) {

    }

    override fun isAlreadyLoadedData(): Boolean = true

    override fun onSupportNavigateUp(): Boolean {
        return navigateUp(R.id.wan_android_navigation_main)
    }
}