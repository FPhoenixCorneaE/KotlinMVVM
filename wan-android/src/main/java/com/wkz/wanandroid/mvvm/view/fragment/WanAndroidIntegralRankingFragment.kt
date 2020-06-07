package com.wkz.wanandroid.mvvm.view.fragment

import com.wkz.wanandroid.R
import kotlinx.android.synthetic.main.wan_android_fragment_integral_ranking.*

/**
 * @desc: 积分排行榜Fragment
 * @date: 2020-06-07 20:27
 */
class WanAndroidIntegralRankingFragment : WanAndroidBaseFragment() {

    override fun getLayoutId(): Int = R.layout.wan_android_fragment_integral_ranking

    override fun initView() {
        // 设置标题栏主题样式
        setCommonTitleBarTheme(mTbTitleBar)
    }

    override fun lazyLoadData() {
    }

    override fun isAlreadyLoadedData(): Boolean = true
}