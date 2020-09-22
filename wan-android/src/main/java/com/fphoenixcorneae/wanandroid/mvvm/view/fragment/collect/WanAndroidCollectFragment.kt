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
        mFlMagicIndicator.bindViewPager2(
            FragmentStatePager2ItemAdapter(
                this,
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
            mVpHome
        )
    }

    override fun lazyLoadData() {
    }

    override fun isAlreadyLoadedData(): Boolean = true
}