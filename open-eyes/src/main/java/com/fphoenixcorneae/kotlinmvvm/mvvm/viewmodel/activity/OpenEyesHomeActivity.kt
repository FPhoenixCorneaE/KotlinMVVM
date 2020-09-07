package com.fphoenixcorneae.kotlinmvvm.mvvm.viewmodel.activity

import android.os.Bundle
import com.fphoenixcorneae.bottomnavigation.BottomNavigationItem
import com.fphoenixcorneae.bottomnavigation.OnBottomNavigationItemClickListener
import com.fphoenixcorneae.framework.base.activity.Dagger2InjectionActivity
import com.fphoenixcorneae.kotlinmvvm.R
import com.fphoenixcorneae.kotlinmvvm.mvvm.contract.OpenEyesHomeContract
import com.fphoenixcorneae.kotlinmvvm.mvvm.presenter.OpenEyesHomePresenter
import com.fphoenixcorneae.kotlinmvvm.mvvm.viewmodel.fragment.OpenEyesHomeFragment
import com.fphoenixcorneae.util.ColorUtil
import com.fphoenixcorneae.util.FragmentUtil
import com.fphoenixcorneae.util.ResourceUtil
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
            .isColoredBackground(true)
            .setTextActiveSize(ResourceUtil.getDimension(R.dimen.sp_13))
            .setTextInactiveSize(ResourceUtil.getDimension(R.dimen.sp_11))
            .setItemActiveColorWithoutColoredBackground(
                ResourceUtil.getColor(R.color.open_eyes_color_black)
            )
            .setFont(ResourceUtil.getFont(R.font.lobster))
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
            .setOnBottomNavigationItemClickListener(object :
                OnBottomNavigationItemClickListener {
                override fun onNavigationItemClick(index: Int) {

                }
            })
    }

    override fun initData(savedInstanceState: Bundle?) {

    }

    override fun isAlreadyLoadedData(): Boolean = true

}
