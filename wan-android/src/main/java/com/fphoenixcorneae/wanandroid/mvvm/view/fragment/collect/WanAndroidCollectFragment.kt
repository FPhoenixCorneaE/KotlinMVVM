package com.fphoenixcorneae.wanandroid.mvvm.view.fragment.collect

import com.fphoenixcorneae.adapter.app.FragmentPagerItems
import com.fphoenixcorneae.adapter.viewpager2.FragmentStatePager2ItemAdapter
import com.fphoenixcorneae.wanandroid.R
import com.fphoenixcorneae.wanandroid.mvvm.view.fragment.WanAndroidBaseFragment
import kotlinx.android.synthetic.main.wan_android_fragment_collect.*

/**
 * @desc: 收藏Fragment
 * @date: 2020-07-30 16:18
 */
class WanAndroidCollectFragment : WanAndroidBaseFragment() {
    override fun getLayoutId(): Int = R.layout.wan_android_fragment_collect

    override fun initView() {
        mTbTitleBar.init()
        initViewPager2AndMagicIndicator(
            FragmentStatePager2ItemAdapter(
                this@WanAndroidCollectFragment,
                FragmentPagerItems.with(mContext)
                    .add(
                        getString(R.string.wan_android_collect_article),
                        WanAndroidCollectArticleFragment::class.java
                    )
                    .add(
                        getString(R.string.wan_android_collect_website),
                        WanAndroidCollectWebsiteFragment::class.java
                    )
                    .create()
            ),
            mVpHome,
            mFlMagicIndicator
        )
    }

    override fun lazyLoadData() {
    }

    override fun isAlreadyLoadedData(): Boolean = true
}