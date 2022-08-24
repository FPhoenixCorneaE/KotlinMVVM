package com.fphoenixcorneae.wanandroid.mvvm.view.fragment

import android.content.res.ColorStateList
import android.graphics.Color
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.fphoenixcorneae.navigation.ImageTextItem
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
    private val mIconResources = listOf(
        R.mipmap.wan_android_ic_menu_home_main,
        R.mipmap.wan_android_ic_menu_home_project,
        R.mipmap.wan_android_ic_menu_home_square,
        R.mipmap.wan_android_ic_menu_home_vipcn,
        R.mipmap.wan_android_ic_menu_home_mine,
    )
    private val mTexts = listOf(
        ResourceUtil.getString(R.string.wan_android_home_main),
        ResourceUtil.getString(R.string.wan_android_home_project),
        ResourceUtil.getString(R.string.wan_android_home_square),
        ResourceUtil.getString(R.string.wan_android_home_vipcn),
        ResourceUtil.getString(R.string.wan_android_home_mine),
    )

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
        mVgNavi.apply {
            // Set background tint
            backgroundTintList = ColorStateList.valueOf(Color.WHITE)
            // Set items
            setImageTextItems(
                mIconResources.flatMapIndexed { index: Int, id: Int ->
                    listOf(ImageTextItem(context).apply {
                        // Set icon
                        setIconResource(id)
                        // Set icon size
                        setIconSize(28f)
                        // Set text size
                        textSize = 16f
                        // Set normal state tint color and selected state tint color
                        setIconTextColor(ResourceUtil.getColor(R.color.wan_android_color_gray_0x666666),
                            ResourceUtil.getColor(R.color.wan_android_colorPrimary))
                        // Set padding between icon and text
                        setIconTextPadding(4f)
                        // Set text
                        text = mTexts[index]
                    })
                }
            )
            // Set animated item size
            setAnimatedItemSize(60f)
            // Set animated item content padding
            setAnimatedItemContentPadding(8f)
            // Set animated item double click
            setOnAnimatedItemDoubleClickListener { position ->
                if (position == 0) {

                }
            }
            // Set item click
            setOnItemClickListener { itemView, position ->
                mVpMain.setCurrentItem(position, false)
            }
        }
    }

    override fun lazyLoadData() {
    }

    override fun isAlreadyLoadedData(): Boolean = true

    override fun initListener() {
    }
}