package com.fphoenixcorneae.wanandroid.mvvm.view.fragment

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.activity.OnBackPressedCallback
import androidx.annotation.AnimRes
import androidx.annotation.AnimatorRes
import androidx.annotation.IdRes
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.fphoenixcorneae.adapter.AnimationType
import com.fphoenixcorneae.adapter.BaseNBAdapter
import com.fphoenixcorneae.adapter.viewpager2.FragmentStatePager2ItemAdapter
import com.fphoenixcorneae.extension.loggerD
import com.fphoenixcorneae.extension.navigate
import com.fphoenixcorneae.extension.navigateUp
import com.fphoenixcorneae.extension.popBackStack
import com.fphoenixcorneae.framework.base.fragment.BaseFragment
import com.fphoenixcorneae.titlebar.CommonTitleBar
import com.fphoenixcorneae.util.ResourceUtil
import com.fphoenixcorneae.util.SizeUtil
import com.fphoenixcorneae.wanandroid.R
import com.fphoenixcorneae.widget.magicindicator.IPagerIndicator
import com.fphoenixcorneae.widget.magicindicator.IPagerTitleView
import com.fphoenixcorneae.widget.magicindicator.MagicIndicator
import com.fphoenixcorneae.widget.magicindicator.adapter.CommonNavigatorAdapter
import com.fphoenixcorneae.widget.magicindicator.helper.ViewPagerHelper
import com.fphoenixcorneae.widget.magicindicator.indicator.LinePagerIndicator
import com.fphoenixcorneae.widget.magicindicator.navigator.CommonNavigator
import com.fphoenixcorneae.widget.magicindicator.titleview.ScaleTransitionPagerTitleView

/**
 * @desc: WanAndroidBaseFragment
 * @date: 2020-06-04 16:20
 */
abstract class WanAndroidBaseFragment : BaseFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 重写返回键,以便于手动退回到上一个Fragment
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    // Handle the back button event
                    loggerD("handleOnBackPressed:${javaClass.name}")
                    onBackPressed()
                }
            }
        )
    }

    /**
     * 初始化标题栏
     */
    protected fun CommonTitleBar.init(
        onTitleBarClickListener: CommonTitleBar.OnTitleBarClickListener? = null
    ) {
        apply {
            setOnTitleBarClickListener(object : CommonTitleBar.OnTitleBarClickListener {
                override fun onClicked(v: View?, action: Int, extra: String?) {
                    when (action) {
                        CommonTitleBar.MotionAction.ACTION_LEFT_BUTTON -> navigateUp()
                    }
                    onTitleBarClickListener?.onClicked(v, action, extra)
                }
            })
        }
    }

    /**
     * 初始化 RecyclerView
     */
    protected fun <T> RecyclerView.init(
        baseNBAdapter: BaseNBAdapter<T>,
        animationType: AnimationType = AnimationType.TRANSLATE_FROM_BOTTOM
    ) {
        baseNBAdapter.showItemAnim(animationType, false)
        apply {
            setHasFixedSize(true)
            isNestedScrollingEnabled = false
            adapter = baseNBAdapter
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

    protected fun navigateNext(
        @IdRes resId: Int,
        args: Bundle? = null,
        navOptions: NavOptions? = navOptions(),
        navigatorExtras: Navigator.Extras? = null
    ) {
        navigate(resId, args, navOptions, navigatorExtras)
    }

    /**
     * NavOptions
     */
    protected fun navOptions(
        @AnimRes @AnimatorRes enterAnim: Int = R.anim.pull_in_right,
        @AnimRes @AnimatorRes exitAnim: Int = R.anim.push_out_left,
        @AnimRes @AnimatorRes popEnterAnim: Int = R.anim.pull_in_left,
        @AnimRes @AnimatorRes popExitAnim: Int = R.anim.push_out_right,
        launchSingleTop: Boolean = true
    ): NavOptions {
        return NavOptions.Builder()
            .setEnterAnim(enterAnim)
            .setExitAnim(exitAnim)
            .setPopEnterAnim(popEnterAnim)
            .setPopExitAnim(popExitAnim)
            .setLaunchSingleTop(launchSingleTop)
            .build()
    }

    /**
     * 点击手机返回键
     */
    open fun onBackPressed() {
        popBackStack()
    }
}