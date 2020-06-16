package com.wkz.wanandroid.mvvm.view.fragment

import android.content.Context
import android.graphics.Typeface
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import com.wkz.adapter.app.FragmentPagerItems
import com.wkz.adapter.viewpager2.FragmentStatePager2ItemAdapter
import com.wkz.util.ResourceUtil
import com.wkz.util.SizeUtil
import com.wkz.util.statusbar.StatusBarUtil
import com.wkz.wanandroid.R
import com.wkz.widget.magicindicator.IPagerIndicator
import com.wkz.widget.magicindicator.IPagerTitleView
import com.wkz.widget.magicindicator.adapter.CommonNavigatorAdapter
import com.wkz.widget.magicindicator.helper.ViewPagerHelper
import com.wkz.widget.magicindicator.indicator.LinePagerIndicator
import com.wkz.widget.magicindicator.navigator.CommonNavigator
import com.wkz.widget.magicindicator.titleview.ScaleTransitionPagerTitleView
import kotlinx.android.synthetic.main.wan_android_fragment_home.*

/**
 * @desc：首页Fragment
 * @date：2020-04-26 12:27
 */
class WanAndroidHomeFragment : WanAndroidBaseFragment() {

    lateinit var mViewPagerAdapter: FragmentStatePager2ItemAdapter

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
        StatusBarUtil.setSmartMargin(mContext, mFlMagicIndicator)
        initViewPager()
        initMagicIndicator()
    }

    override fun initListener() {

    }

    private fun initViewPager() {
        mViewPagerAdapter = FragmentStatePager2ItemAdapter(
            this,
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
        )
        mVpHome.apply {
            adapter = mViewPagerAdapter
        }
    }

    private fun initMagicIndicator() {
        val commonNavigator = CommonNavigator(mContext)
        commonNavigator.apply {
            isAdjustMode = true
            isSkimOver = true
            adapter = object : CommonNavigatorAdapter() {
                override val count: Int
                    get() = mViewPagerAdapter.itemCount

                override fun getTitleView(context: Context, index: Int): IPagerTitleView {
                    return ScaleTransitionPagerTitleView(context).apply {
                        text = mViewPagerAdapter.getPageTitle(index).toString()
                        textSize = 18f
                        normalColor =
                            ResourceUtil.getColor(R.color.wan_android_color_title_0x222222)
                        selectedColor = ResourceUtil.getColor(R.color.wan_android_colorAccent)
                        typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                        setOnClickListener { mVpHome.currentItem = index }
                    }
                }

                override fun getIndicator(context: Context): IPagerIndicator {
                    return LinePagerIndicator(context).apply {
                        mode = LinePagerIndicator.MODE_EXACTLY
                        lineHeight = SizeUtil.dpToPx(5f)
                        lineWidth = SizeUtil.dpToPx(40f)
                        roundRadius = SizeUtil.dpToPx(6f)
                        startInterpolator = AccelerateInterpolator()
                        endInterpolator = DecelerateInterpolator(2.0f)
                        setColors(ResourceUtil.getColor(R.color.wan_android_colorAccent))
                    }
                }
            }
        }
        mFlMagicIndicator.navigator = commonNavigator
        ViewPagerHelper.bind(mFlMagicIndicator, mVpHome)
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