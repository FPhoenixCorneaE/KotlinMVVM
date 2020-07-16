package com.wkz.wanandroid.mvvm.view.fragment

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.NavHostFragment
import androidx.viewpager2.widget.ViewPager2
import com.wkz.adapter.viewpager2.FragmentStatePager2ItemAdapter
import com.wkz.extension.navigateUp
import com.wkz.extension.popBackStack
import com.wkz.framework.base.fragment.BaseFragment
import com.wkz.titlebar.CommonTitleBar
import com.wkz.util.ImageUtil
import com.wkz.util.ResourceUtil
import com.wkz.util.SizeUtil
import com.wkz.wanandroid.R
import com.wkz.widget.magicindicator.IPagerIndicator
import com.wkz.widget.magicindicator.IPagerTitleView
import com.wkz.widget.magicindicator.MagicIndicator
import com.wkz.widget.magicindicator.adapter.CommonNavigatorAdapter
import com.wkz.widget.magicindicator.helper.ViewPagerHelper
import com.wkz.widget.magicindicator.indicator.LinePagerIndicator
import com.wkz.widget.magicindicator.navigator.CommonNavigator
import com.wkz.widget.magicindicator.titleview.ScaleTransitionPagerTitleView

/**
 * @desc: WanAndroidBaseFragment
 * @date: 2020-06-04 16:20
 */
abstract class WanAndroidBaseFragment : BaseFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 重写返回键,以便于手动退回到上一个Fragment
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    // Handle the back button event
                    popBackStack()
                }
            }
        )
    }

    /**
     * 设置标题栏主题样式
     */
    protected fun setCommonTitleBarTheme(commonTitleBar: CommonTitleBar?) {
        commonTitleBar?.apply {
            leftImageButton?.let {
                ImageUtil.setTintColor(
                    it,
                    ResourceUtil.getColor(R.color.wan_android_color_title_0x222222)
                )
            }
            setOnTitleBarClickListener(object : CommonTitleBar.OnTitleBarClickListener {
                override fun onClicked(v: View?, action: Int, extra: String?) {
                    when (action) {
                        CommonTitleBar.MotionAction.ACTION_LEFT_BUTTON -> navigateUp()
                    }
                }
            })
        }
    }

    /**
     * 初始化 ViewPager2 & MagicIndicator
     */
    protected fun initViewPager2AndMagicIndicator(
        fragmentStatePager2ItemAdapter: FragmentStatePager2ItemAdapter,
        viewPager2: ViewPager2,
        magicIndicator: MagicIndicator,
        adjustMode: Boolean = false
    ) {
        viewPager2.apply {
            offscreenPageLimit = when {
                fragmentStatePager2ItemAdapter.itemCount > 1 -> {
                    fragmentStatePager2ItemAdapter.itemCount
                }
                else -> 1
            }
            adapter = fragmentStatePager2ItemAdapter
        }
        val commonNavigator = CommonNavigator(mContext)
        commonNavigator.apply {
            isAdjustMode = adjustMode
            isSkimOver = true
            adapter = object : CommonNavigatorAdapter() {
                override val count: Int
                    get() = fragmentStatePager2ItemAdapter.itemCount

                override fun getTitleView(context: Context, index: Int): IPagerTitleView {
                    return ScaleTransitionPagerTitleView(context).apply {
                        text = fragmentStatePager2ItemAdapter.getPageTitle(index).toString()
                        textSize = 18f
                        normalColor =
                            ResourceUtil.getColor(R.color.wan_android_color_title_0x222222)
                        selectedColor = ResourceUtil.getColor(R.color.wan_android_colorAccent)
                        typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                        setOnClickListener { viewPager2.currentItem = index }
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
        magicIndicator.navigator = commonNavigator
        ViewPagerHelper.bind(magicIndicator, viewPager2)
    }
}