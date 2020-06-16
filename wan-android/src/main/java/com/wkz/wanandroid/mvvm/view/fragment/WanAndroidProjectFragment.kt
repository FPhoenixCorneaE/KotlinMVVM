package com.wkz.wanandroid.mvvm.view.fragment

import android.content.Context
import android.graphics.Typeface
import android.text.Html
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.lifecycle.Observer
import com.wkz.adapter.app.FragmentPagerItems
import com.wkz.adapter.viewpager2.FragmentStatePager2ItemAdapter
import com.wkz.extension.loggerJson
import com.wkz.extension.viewModel
import com.wkz.util.BundleBuilder
import com.wkz.util.ResourceUtil
import com.wkz.util.SizeUtil
import com.wkz.util.gson.GsonUtil
import com.wkz.util.statusbar.StatusBarUtil
import com.wkz.wanandroid.R
import com.wkz.wanandroid.constant.WanAndroidConstant
import com.wkz.wanandroid.mvvm.model.WanAndroidProjectClassifyBean
import com.wkz.wanandroid.mvvm.viewmodel.WanAndroidProjectViewModel
import com.wkz.widget.magicindicator.IPagerIndicator
import com.wkz.widget.magicindicator.IPagerTitleView
import com.wkz.widget.magicindicator.adapter.CommonNavigatorAdapter
import com.wkz.widget.magicindicator.helper.ViewPagerHelper
import com.wkz.widget.magicindicator.indicator.LinePagerIndicator
import com.wkz.widget.magicindicator.navigator.CommonNavigator
import com.wkz.widget.magicindicator.titleview.ScaleTransitionPagerTitleView
import kotlinx.android.synthetic.main.wan_android_fragment_project.*

/**
 * @desc: 项目Fragment
 * @date: 2020-06-14 17:31
 */
class WanAndroidProjectFragment : WanAndroidBaseFragment() {

    /* 项目ViewModel */
    private val mProjectViewModel by viewModel<WanAndroidProjectViewModel>()

    /* 项目分类数据 */
    private val mClassifyData = arrayListOf<WanAndroidProjectClassifyBean>()
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
    override fun getLayoutId(): Int = R.layout.wan_android_fragment_project

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
        mVpProject.apply {
            offscreenPageLimit = 1
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
                        setOnClickListener { mVpProject.currentItem = index }
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
        ViewPagerHelper.bind(mFlMagicIndicator, mVpProject)
    }

    override fun initListener() {
        mProjectViewModel.apply {
            mProjectClassify.observe(viewLifecycleOwner, Observer {
                it?.apply {
                    mClassifyData.clear()
                    mClassifyData.add(WanAndroidProjectClassifyBean(name = getString(R.string.wan_android_project_newest)))
                    mClassifyData.addAll(it)

                    mFragmentPagerCreator.let {
                        mClassifyData.forEach { classifyBean ->
                            it.add(
                                Html.fromHtml(classifyBean.name),
                                WanAndroidProjectChildFragment::class.java,
                                BundleBuilder.of()
                                    .putInt(
                                        WanAndroidConstant.WAN_ANDROID_CLASSIFY_ID,
                                        classifyBean.id
                                    )
                                    .putBoolean(
                                        WanAndroidConstant.WAN_ANDROID_NEWEST_PROJECT,
                                        classifyBean.id == 0
                                    )
                                    .get()
                            )
                        }
                    }
                    mFlMagicIndicator.navigator?.notifyDataSetChanged()
                    mViewPagerAdapter.notifyDataSetChanged()
                }
            })
        }
    }

    /**
     * 懒加载数据
     */
    override fun lazyLoadData() {
        // 获取项目分类
        mProjectViewModel.getProjectClassify()
    }

    override fun isAlreadyLoadedData(): Boolean = mClassifyData.isNotEmpty()

    companion object {
        fun getInstance(): WanAndroidProjectFragment {
            return WanAndroidProjectFragment()
        }
    }
}