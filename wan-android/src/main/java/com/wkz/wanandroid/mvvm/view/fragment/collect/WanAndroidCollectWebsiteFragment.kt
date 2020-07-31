package com.wkz.wanandroid.mvvm.view.fragment.collect

import com.wkz.wanandroid.R
import com.wkz.wanandroid.mvvm.view.fragment.WanAndroidBaseFragment

/**
 * @desc: 收藏网址Fragment
 * @date: 2020-07-30 16:31
 */
class WanAndroidCollectWebsiteFragment : WanAndroidBaseFragment() {
    override fun getLayoutId(): Int = R.layout.wan_android_fragment_collect_website

    override fun initView() {
    }

    override fun lazyLoadData() {
    }

    override fun isAlreadyLoadedData(): Boolean = true
}