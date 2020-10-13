package com.fphoenixcorneae.wanandroid.mvvm.view.fragment.square

import com.fphoenixcorneae.viewpager.FragmentPagerItems
import com.fphoenixcorneae.viewpager.FragmentStatePager2ItemAdapter
import com.fphoenixcorneae.util.statusbar.StatusBarUtil
import com.fphoenixcorneae.wanandroid.R
import com.fphoenixcorneae.wanandroid.mvvm.view.fragment.WanAndroidBaseFragment
import kotlinx.android.synthetic.main.wan_android_fragment_square.*

/**
 * @desc: 广场Fragment
 * @date: 2020-06-19 16:50
 */
class WanAndroidSquareFragment : WanAndroidBaseFragment() {

    private val mViewPagerAdapter by lazy {
        FragmentStatePager2ItemAdapter(
            this,
            FragmentPagerItems.with(mContext)
                .add(
                    getString(R.string.wan_android_square_article),
                    WanAndroidSquareArticleFragment::class.java
                )
                .add(
                    getString(R.string.wan_android_square_ask),
                    WanAndroidSquareAskFragment::class.java
                )
                .add(
                    getString(R.string.wan_android_square_system),
                    WanAndroidSquareSystemFragment::class.java
                )
                .add(
                    getString(R.string.wan_android_square_navigation),
                    WanAndroidSquareNavigationFragment::class.java
                )
                .create()
        )
    }

    /**
     * 加载布局
     */
    override fun getLayoutId(): Int = R.layout.wan_android_fragment_square

    /**
     * 初始化View
     */
    override fun initView() {
        // 模拟状态栏
        StatusBarUtil.setSmartPadding(mContext, mVwStatusBar)
        StatusBarUtil.setSmartMargin(mContext, mLlIndicatorContainer)
        mFlMagicIndicator.bindViewPager2(mViewPagerAdapter, mVpSquare)
    }

    override fun initListener() {

    }

    /**
     * 懒加载数据
     */
    override fun lazyLoadData() {

    }

    override fun isAlreadyLoadedData(): Boolean = true

    companion object {
        fun getInstance(): WanAndroidSquareFragment {
            return WanAndroidSquareFragment()
        }
    }
}