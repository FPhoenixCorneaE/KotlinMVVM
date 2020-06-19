package com.wkz.wanandroid.mvvm.view.fragment

import android.content.Context
import android.graphics.Typeface
import android.text.Html
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.lifecycle.Observer
import com.wkz.adapter.app.FragmentPagerItems
import com.wkz.adapter.viewpager2.FragmentStatePager2ItemAdapter
import com.wkz.extension.viewModel
import com.wkz.util.BundleBuilder
import com.wkz.util.ResourceUtil
import com.wkz.util.SizeUtil
import com.wkz.util.statusbar.StatusBarUtil
import com.wkz.wanandroid.R
import com.wkz.wanandroid.constant.WanAndroidConstant
import com.wkz.wanandroid.mvvm.model.WanAndroidClassifyBean
import com.wkz.wanandroid.mvvm.viewmodel.WanAndroidVipcnViewModel
import com.wkz.widget.magicindicator.IPagerIndicator
import com.wkz.widget.magicindicator.IPagerTitleView
import com.wkz.widget.magicindicator.adapter.CommonNavigatorAdapter
import com.wkz.widget.magicindicator.helper.ViewPagerHelper
import com.wkz.widget.magicindicator.indicator.LinePagerIndicator
import com.wkz.widget.magicindicator.navigator.CommonNavigator
import com.wkz.widget.magicindicator.titleview.ScaleTransitionPagerTitleView
import kotlinx.android.synthetic.main.wan_android_fragment_vipcn.*

/**
 * @desc: 公众号Fragment
 * @date: 2020-06-18 17:31
 */
class WanAndroidVipcnFragment : WanAndroidBaseFragment() {

    /* 公众号ViewModel */
    private val mVipcnViewModel by viewModel<WanAndroidVipcnViewModel>()

    /* 公众号分类数据 */
    private val mClassifyData = arrayListOf<WanAndroidClassifyBean>()
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
    override fun getLayoutId(): Int = R.layout.wan_android_fragment_vipcn

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

    private fun initViewPager() {
        mVpVipcn.apply {
            adapter = mViewPagerAdapter
        }
    }

    private fun initMagicIndicator() {
        val commonNavigator = CommonNavigator(mContext)
        commonNavigator.apply {
            isAdjustMode = false
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
                        setOnClickListener { mVpVipcn.currentItem = index }
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
        ViewPagerHelper.bind(mFlMagicIndicator, mVpVipcn)
    }

    override fun initListener() {
        mVipcnViewModel.apply {
            mVipcnClassify.observe(viewLifecycleOwner, Observer {
                it?.apply {
                    mClassifyData.clear()
                    mClassifyData.addAll(it)

                    mFragmentPagerCreator.create().clear()
                    mFragmentPagerCreator.let {
                        mClassifyData.forEach { classifyBean ->
                            it.add(
                                Html.fromHtml(classifyBean.name),
                                WanAndroidVipcnChildFragment::class.java,
                                BundleBuilder.of()
                                    .putInt(
                                        WanAndroidConstant.WAN_ANDROID_CLASSIFY_ID,
                                        classifyBean.id
                                    )
                                    .get()
                            )
                        }
                    }
                    mFlMagicIndicator.navigator?.notifyDataSetChanged()
                    mVpVipcn.adapter?.notifyDataSetChanged()
                    mVpVipcn.offscreenPageLimit = mViewPagerAdapter.itemCount
                }
            })
        }
    }

    /**
     * 懒加载数据
     */
    override fun lazyLoadData() {
        // 获取公众号分类
        mVipcnViewModel.getVipcnClassify()
    }

    override fun isAlreadyLoadedData(): Boolean = mClassifyData.isNotEmpty()

    companion object {
        fun getInstance(): WanAndroidVipcnFragment {
            return WanAndroidVipcnFragment()
        }
    }
}