package com.fphoenixcorneae.kotlinmvvm.mvvm.viewmodel.fragment

import android.content.Context
import android.graphics.Typeface
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.viewpager2.widget.ViewPager2
import com.fphoenixcorneae.adapter.viewpager2.FragmentStatePager2ItemAdapter
import com.fphoenixcorneae.framework.base.fragment.AutoDisposeFragment
import com.fphoenixcorneae.kotlinmvvm.R
import com.fphoenixcorneae.util.ResourceUtil
import com.fphoenixcorneae.util.SizeUtil
import com.fphoenixcorneae.widget.magicindicator.IPagerIndicator
import com.fphoenixcorneae.widget.magicindicator.IPagerTitleView
import com.fphoenixcorneae.widget.magicindicator.MagicIndicator
import com.fphoenixcorneae.widget.magicindicator.adapter.CommonNavigatorAdapter
import com.fphoenixcorneae.widget.magicindicator.helper.bindViewPager2
import com.fphoenixcorneae.widget.magicindicator.indicator.LinePagerIndicator
import com.fphoenixcorneae.widget.magicindicator.navigator.CommonNavigator
import com.fphoenixcorneae.widget.magicindicator.titleview.ScaleTransitionPagerTitleView

/**
 * @desc OpenEyesBaseFragment
 * @date 2020-09-18 17:14
 */
abstract class OpenEyesBaseFragment : AutoDisposeFragment() {

    /**
     * 初始化 ViewPager2 & MagicIndicator
     */
    protected fun MagicIndicator.bindViewPager2(
        fragmentStatePager2ItemAdapter: FragmentStatePager2ItemAdapter,
        viewPager2: ViewPager2,
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
        navigator = CommonNavigator(mContext).apply {
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
                            ResourceUtil.getColor(R.color.open_eyes_color_title_0x222222)
                        selectedColor = ResourceUtil.getColor(R.color.open_eyes_colorAccent)
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
                        setColors(ResourceUtil.getColor(R.color.open_eyes_colorAccent))
                    }
                }
            }
        }
        bindViewPager2(viewPager2)
    }
}