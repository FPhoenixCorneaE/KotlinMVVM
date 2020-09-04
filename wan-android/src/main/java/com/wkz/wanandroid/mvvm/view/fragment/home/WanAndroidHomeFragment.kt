package com.wkz.wanandroid.mvvm.view.fragment.home

import androidx.navigation.NavOptions
import com.wkz.adapter.app.FragmentPagerItems
import com.wkz.adapter.viewpager2.FragmentStatePager2ItemAdapter
import com.wkz.framework.navigation.FragmentNavigatorExtras
import com.wkz.util.statusbar.StatusBarUtil
import com.wkz.wanandroid.R
import com.wkz.wanandroid.mvvm.view.fragment.WanAndroidBaseFragment
import kotlinx.android.synthetic.main.wan_android_fragment_home.*

/**
 * @desc：首页Fragment
 * @date：2020-04-26 12:27
 */
class WanAndroidHomeFragment : WanAndroidBaseFragment() {

    /**
     * 加载布局
     */
    override fun getLayoutId(): Int = R.layout.wan_android_fragment_home

    /**
     * 初始化View
     */
    override fun initView() {
        // 模拟状态栏
        StatusBarUtil.setSmartPadding(mContext, mVwStatusBar)
        StatusBarUtil.setSmartMargin(mContext, mFlIndicatorContainer)
        initViewPager2AndMagicIndicator(
            FragmentStatePager2ItemAdapter(
                this@WanAndroidHomeFragment,
                FragmentPagerItems.with(mContext)
                    .add(
                        getString(R.string.wan_android_title_fragment_home_article),
                        WanAndroidHomeArticleFragment::class.java
                    )
                    .add(
                        getString(R.string.wan_android_title_fragment_home_qa),
                        WanAndroidHomeQaFragment::class.java
                    )
                    .create()
            ),
            mVpHome,
            mFlMagicIndicator
        )
    }

    override fun initListener() {
        mIvSearch.setOnClickListener {
            navigateNext(
                R.id.searchFragment,
                navOptions = NavOptions.Builder().setLaunchSingleTop(true).build(),
                navigatorExtras = FragmentNavigatorExtras(
                    mIvSearch to mIvSearch.transitionName
                )
            )
        }
    }

    /**
     * 懒加载数据
     */
    override fun lazyLoadData() {

    }

    override fun isAlreadyLoadedData(): Boolean = true

    companion object {
        fun getInstance(): WanAndroidHomeFragment {
            return WanAndroidHomeFragment()
        }
    }
}