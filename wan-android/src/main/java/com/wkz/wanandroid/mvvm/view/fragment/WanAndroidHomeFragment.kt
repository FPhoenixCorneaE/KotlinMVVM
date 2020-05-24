package com.wkz.wanandroid.mvvm.view.fragment

import android.content.Context
import android.graphics.Typeface
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import com.wkz.adapter.app.FragmentPagerItems
import com.wkz.adapter.app.FragmentStatePagerItemAdapter
import com.wkz.framework.base.fragment.BaseFragment
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
class WanAndroidHomeFragment : BaseFragment() {

    lateinit var mViewPagerAdapter: FragmentStatePagerItemAdapter

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
        mViewPagerAdapter = FragmentStatePagerItemAdapter(
            childFragmentManager,
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
                    get() = mViewPagerAdapter.count

                override fun getTitleView(context: Context, index: Int): IPagerTitleView {
                    val simplePagerTitleView = ScaleTransitionPagerTitleView(context)
                    simplePagerTitleView.text =
                        mViewPagerAdapter.getPageTitle(index).toString()
                    simplePagerTitleView.textSize = 18f
                    simplePagerTitleView.normalColor =
                        ResourceUtil.getColor(R.color.wan_android_color_title_0x222222)
                    simplePagerTitleView.selectedColor =
                        ResourceUtil.getColor(R.color.wan_android_colorAccent)
                    simplePagerTitleView.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                    simplePagerTitleView.setOnClickListener { mVpHome.currentItem = index }
                    return simplePagerTitleView
                }

                override fun getIndicator(context: Context): IPagerIndicator {
                    val indicator = LinePagerIndicator(context)
                    indicator.mode = LinePagerIndicator.MODE_EXACTLY
                    indicator.lineHeight = SizeUtil.dp2px(5f).toFloat()
                    indicator.lineWidth = SizeUtil.dp2px(40f).toFloat()
                    indicator.roundRadius = SizeUtil.dp2px(5f).toFloat()
                    indicator.startInterpolator = AccelerateInterpolator()
                    indicator.endInterpolator = DecelerateInterpolator(2.0f)
                    indicator.setColors(ResourceUtil.getColor(R.color.wan_android_colorAccent))
                    return indicator
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