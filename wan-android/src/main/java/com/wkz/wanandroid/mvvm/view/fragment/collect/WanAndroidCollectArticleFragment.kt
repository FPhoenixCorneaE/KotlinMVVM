package com.wkz.wanandroid.mvvm.view.fragment.collect

import com.wkz.wanandroid.R
import com.wkz.wanandroid.mvvm.view.fragment.WanAndroidBaseFragment

/**
 * @desc: 收藏文章Fragment
 * @date: 2020-07-30 16:31
 */
class WanAndroidCollectArticleFragment : WanAndroidBaseFragment() {
    override fun getLayoutId(): Int = R.layout.wan_android_fragment_collect_article

    override fun initView() {
    }

    override fun lazyLoadData() {
    }

    override fun isAlreadyLoadedData(): Boolean = true

    companion object {
        fun getInstance(): WanAndroidCollectArticleFragment {
            return WanAndroidCollectArticleFragment()
        }
    }
}