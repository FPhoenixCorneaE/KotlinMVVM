package com.wkz.wanandroid.mvvm.view.fragment

import com.wkz.adapter.app.FragmentPagerItems
import com.wkz.adapter.viewpager2.FragmentStatePager2ItemAdapter
import com.wkz.extension.viewModel
import com.wkz.util.statusbar.StatusBarUtil
import com.wkz.wanandroid.R
import com.wkz.wanandroid.mvvm.viewmodel.WanAndroidSquareViewModel
import kotlinx.android.synthetic.main.wan_android_fragment_square.*

/**
 * @desc: 广场Fragment
 * @date: 2020-06-19 16:50
 */
class WanAndroidSquareFragment : WanAndroidBaseFragment() {

    /* 广场ViewModel */
    private val mSquareViewModel by viewModel<WanAndroidSquareViewModel>()

    private val mFragmentPagerCreator by lazy {
        FragmentPagerItems.with(mContext)
    }
    private val mViewPagerAdapter by lazy {
        FragmentStatePager2ItemAdapter(
            this,
            mFragmentPagerCreator.create()
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
        StatusBarUtil.setSmartMargin(mContext, mFlMagicIndicator)
        initViewPager2AndMagicIndicator(mViewPagerAdapter, mVpSquare, mFlMagicIndicator)
    }

    override fun initListener() {
        mSquareViewModel.apply {

        }
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