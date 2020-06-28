package com.wkz.wanandroid.mvvm.view.fragment

import com.wkz.wanandroid.R

/**
 * @desc: 搜索Fragment
 * @date: 2020-06-28 14:31
 */
class WanAndroidSearchFragment : WanAndroidBaseFragment() {

    override fun getLayoutId(): Int = R.layout.wan_android_fragment_search

    override fun initView() {
    }

    override fun lazyLoadData() {
    }

    override fun isAlreadyLoadedData(): Boolean = true
}