package com.wkz.wanandroid.mvvm.view.fragment.search

import com.wkz.wanandroid.R
import com.wkz.wanandroid.mvvm.view.fragment.WanAndroidBaseFragment

/**
 * @desc：搜索结果Fragment
 * @date：2020-07-27 10:00
 */
class WanAndroidSearchResultFragment : WanAndroidBaseFragment() {

    override fun getLayoutId(): Int = R.layout.wan_android_fragment_search_result

    override fun initView() {
    }

    override fun lazyLoadData() {
    }

    override fun isAlreadyLoadedData(): Boolean = true
}