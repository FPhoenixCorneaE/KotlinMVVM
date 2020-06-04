package com.wkz.wanandroid.mvvm.view.fragment

import com.wkz.wanandroid.R

class WanAndroidProjectFragment : WanAndroidBaseFragment() {
    /**
     * 加载布局
     */
    override fun getLayoutId(): Int = R.layout.wan_android_fragment_home

    /**
     * 初始化View
     */
    override fun initView() {

    }

    /**
     * 懒加载数据
     */
    override fun lazyLoadData() {

    }

    override fun isAlreadyLoadedData(): Boolean = true
}