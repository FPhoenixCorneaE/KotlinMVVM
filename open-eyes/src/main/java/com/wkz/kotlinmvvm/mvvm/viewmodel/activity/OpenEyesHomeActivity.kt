package com.wkz.kotlinmvvm.mvvm.viewmodel.activity

import android.os.Bundle
import com.wkz.bottomnavigation.BottomNavigationItem
import com.wkz.bottomnavigation.OnBottomNavigationItemClickListener
import com.wkz.framework.base.activity.Dagger2InjectionActivity
import com.wkz.kotlinmvvm.R
import com.wkz.kotlinmvvm.mvvm.contract.OpenEyesHomeContract
import com.wkz.kotlinmvvm.mvvm.presenter.OpenEyesHomePresenter
import com.wkz.kotlinmvvm.mvvm.viewmodel.fragment.OpenEyesHomeFragment
import com.wkz.util.ColorUtil
import com.wkz.util.FragmentUtil
import com.wkz.util.ResourceUtil
import kotlinx.android.synthetic.main.open_eyes_activity_home.*

class OpenEyesHomeActivity :
    Dagger2InjectionActivity<OpenEyesHomeContract.View, OpenEyesHomePresenter>() {

    override fun getLayoutId(): Int = R.layout.open_eyes_activity_home

    override fun initView() {
        initBottomNavigationView()
        FragmentUtil.with(this)
            .setContainerViewId(R.id.mFlContainer)
            .setCustomAnimations(R.anim.bottom_in, R.anim.bottom_out)
            .addFragment(OpenEyesHomeFragment.getInstance())
            .commit()
    }

    private fun initBottomNavigationView() {
        mBnvNavigation.isWithText(false)
        mBnvNavigation.isColoredBackground(true)
        mBnvNavigation.setTextActiveSize(ResourceUtil.getDimension(R.dimen.sp_13))
        mBnvNavigation.setTextInactiveSize(ResourceUtil.getDimension(R.dimen.sp_11))
        mBnvNavigation.setItemActiveColorWithoutColoredBackground(
            ResourceUtil.getColor(R.color.open_eyes_color_darker_gray)
        )
        mBnvNavigation.setFont(ResourceUtil.getFont(R.font.lobster))
        mBnvNavigation.setTabs(
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
        mBnvNavigation.setOnBottomNavigationItemClickListener(object :
            OnBottomNavigationItemClickListener {
            override fun onNavigationItemClick(index: Int) {

            }
        })
    }

    override fun initData(savedInstanceState: Bundle?) {

    }

    override fun isAlreadyLoadedData(): Boolean = true

}
