package com.fphoenixcorneae.kotlinmvvm.mvvm.viewmodel.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.fphoenixcorneae.bottomnavigation.BottomNavigationItem
import com.fphoenixcorneae.framework.base.activity.Dagger2InjectionActivity
import com.fphoenixcorneae.kotlinmvvm.R
import com.fphoenixcorneae.kotlinmvvm.mvvm.contract.OpenEyesHomeContract
import com.fphoenixcorneae.kotlinmvvm.mvvm.presenter.OpenEyesHomePresenter
import com.fphoenixcorneae.kotlinmvvm.mvvm.viewmodel.fragment.OpenEyesDiscoveryFragment
import com.fphoenixcorneae.kotlinmvvm.mvvm.viewmodel.fragment.OpenEyesHomeFragment
import com.fphoenixcorneae.util.ColorUtil
import com.fphoenixcorneae.util.FragmentUtil
import com.fphoenixcorneae.util.ResourceUtil
import kotlinx.android.synthetic.main.open_eyes_activity_home.*

class OpenEyesHomeActivity :
    Dagger2InjectionActivity<OpenEyesHomeContract.View, OpenEyesHomePresenter>() {

    private val mFragments = arrayListOf(
        OpenEyesHomeFragment.getInstance(),
        OpenEyesDiscoveryFragment.getInstance(),
        OpenEyesHomeFragment.getInstance(),
        OpenEyesDiscoveryFragment.getInstance()
    )
    private var mPreviousFragment: Fragment? = null

    override fun getLayoutId(): Int = R.layout.open_eyes_activity_home

    override fun initView() {
        initBottomNavigationView()
        showFragment(mFragments[0])
    }

    private fun initBottomNavigationView() {
        mBnvNavigation.isWithText(false)
            .isColoredBackground(true)
            .setTextSize(
                ResourceUtil.getDimension(R.dimen.sp_14),
                ResourceUtil.getDimension(R.dimen.sp_12)
            )
            .setItemColor(
                ResourceUtil.getColor(R.color.open_eyes_color_black),
                ResourceUtil.getColor(R.color.open_eyes_color_darker_gray)
            )
            .setFont(ResourceUtil.getFont(R.font.framework_slender_gold))
            .setTabs(
                listOf(
                    BottomNavigationItem(
                        "首页",
                        ColorUtil.setAlphaComponent(ColorUtil.randomColor, 0.1f),
                        R.drawable.open_eyes_ic_nav_home
                    ),
                    BottomNavigationItem(
                        "发现",
                        ColorUtil.setAlphaComponent(ColorUtil.randomColor, 0.1f),
                        R.drawable.open_eyes_ic_nav_discover
                    ),
                    BottomNavigationItem(
                        "热门",
                        ColorUtil.setAlphaComponent(ColorUtil.randomColor, 0.1f),
                        R.drawable.open_eyes_ic_nav_hot
                    ),
                    BottomNavigationItem(
                        "我的",
                        ColorUtil.setAlphaComponent(ColorUtil.randomColor, 0.1f),
                        R.drawable.open_eyes_ic_nav_mine
                    )
                )
            )
            .setOnBottomNavigationItemClickListener { index ->
                showFragment(mFragments[index])
            }
    }

    private fun showFragment(newFragment: Fragment) {
        FragmentUtil.with(this)
            .setContainerViewId(R.id.mFlContainer)
            .setCustomAnimations(R.anim.bottom_in, R.anim.bottom_out)
            .hideAndShowFragment(mPreviousFragment, newFragment)
            .commit()
        mPreviousFragment = newFragment
    }

    override fun initData(savedInstanceState: Bundle?) {

    }

    override fun isAlreadyLoadedData(): Boolean = true

}
