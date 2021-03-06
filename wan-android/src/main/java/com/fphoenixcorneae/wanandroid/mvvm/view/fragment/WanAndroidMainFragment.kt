package com.fphoenixcorneae.wanandroid.mvvm.view.fragment

import android.view.View
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.fphoenixcorneae.animated_bottom_view.AnimatedNavigationItem
import com.fphoenixcorneae.animated_bottom_view.OnNavigationItemClickListener
import com.fphoenixcorneae.util.ContextUtil
import com.fphoenixcorneae.util.ResourceUtil
import com.fphoenixcorneae.wanandroid.R
import com.fphoenixcorneae.wanandroid.mvvm.view.fragment.home.WanAndroidHomeFragment
import com.fphoenixcorneae.wanandroid.mvvm.view.fragment.mine.WanAndroidMineFragment
import com.fphoenixcorneae.wanandroid.mvvm.view.fragment.project.WanAndroidProjectFragment
import com.fphoenixcorneae.wanandroid.mvvm.view.fragment.square.WanAndroidSquareFragment
import com.fphoenixcorneae.wanandroid.mvvm.view.fragment.vipcn.WanAndroidVipcnFragment
import kotlinx.android.synthetic.main.wan_android_fragment_main.*

/**
 * @desc: 首页Fragment
 * @date: 2020-06-02 16:55
 */
class WanAndroidMainFragment : WanAndroidBaseFragment() {

    private val mFragments by lazy {
        listOf(
            WanAndroidHomeFragment.getInstance(),
            WanAndroidProjectFragment.getInstance(),
            WanAndroidSquareFragment.getInstance(),
            WanAndroidVipcnFragment.getInstance(),
            WanAndroidMineFragment.getInstance()
        )
    }

    /**
     *  加载布局
     */
    override fun getLayoutId(): Int = R.layout.wan_android_fragment_main

    /**
     * 初始化 View
     */
    override fun initView() {
        mVpMain.run {
            offscreenPageLimit = mFragments.size
            // 是否可滑动
            this.isUserInputEnabled = false
            // 设置适配器
            adapter = object : FragmentStateAdapter(this@WanAndroidMainFragment) {
                override fun createFragment(position: Int) = mFragments[position]
                override fun getItemCount() = mFragments.size
            }
            ContextUtil.runOnUiThreadDelayed(Runnable {
                // 直接设置当前Item不生效,需要延迟设置
                setCurrentItem(0, false)
            }, 300)
        }
        mBnvMain.addImageButtons(
            arrayOf(
                AnimatedNavigationItem(
                    R.mipmap.wan_android_ic_menu_home_main,
                    getString(R.string.wan_android_home_main),
                    ResourceUtil.getColor(R.color.wan_android_color_gray_0x666666),
                    ResourceUtil.getColor(R.color.wan_android_colorPrimary)
                ),
                AnimatedNavigationItem(
                    R.mipmap.wan_android_ic_menu_home_project,
                    getString(R.string.wan_android_home_project),
                    ResourceUtil.getColor(R.color.wan_android_color_gray_0x666666),
                    ResourceUtil.getColor(R.color.wan_android_colorPrimary)
                ),
                AnimatedNavigationItem(
                    R.mipmap.wan_android_ic_menu_home_square,
                    getString(R.string.wan_android_home_square),
                    ResourceUtil.getColor(R.color.wan_android_color_gray_0x666666),
                    ResourceUtil.getColor(R.color.wan_android_colorPrimary)
                ),
                AnimatedNavigationItem(
                    R.mipmap.wan_android_ic_menu_home_vipcn,
                    getString(R.string.wan_android_home_vipcn),
                    ResourceUtil.getColor(R.color.wan_android_color_gray_0x666666),
                    ResourceUtil.getColor(R.color.wan_android_colorPrimary)
                ),
                AnimatedNavigationItem(
                    R.mipmap.wan_android_ic_menu_home_mine,
                    getString(R.string.wan_android_home_mine),
                    ResourceUtil.getColor(R.color.wan_android_color_gray_0x666666),
                    ResourceUtil.getColor(R.color.wan_android_colorPrimary)
                )
            )
        )
    }

    override fun lazyLoadData() {
    }

    override fun isAlreadyLoadedData(): Boolean = true

    override fun initListener() {
        mBnvMain.onNavigationItemClickListener = object : OnNavigationItemClickListener {
            override fun onItemClick(itemView: View, index: Int) {
                mVpMain.setCurrentItem(index, false)
            }
        }
    }
}