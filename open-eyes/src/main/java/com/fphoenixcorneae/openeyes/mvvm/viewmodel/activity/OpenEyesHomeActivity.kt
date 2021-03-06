package com.fphoenixcorneae.openeyes.mvvm.viewmodel.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.fphoenixcorneae.bottomnavigation.BottomNavigationItem
import com.fphoenixcorneae.openeyes.R
import com.fphoenixcorneae.openeyes.mvvm.contract.OpenEyesHomeContract
import com.fphoenixcorneae.openeyes.mvvm.presenter.OpenEyesHomePresenter
import com.fphoenixcorneae.openeyes.mvvm.viewmodel.fragment.OpenEyesDiscoveryFragment
import com.fphoenixcorneae.openeyes.mvvm.viewmodel.fragment.OpenEyesHomeFragment
import com.fphoenixcorneae.openeyes.mvvm.viewmodel.fragment.OpenEyesHotFragment
import com.fphoenixcorneae.openeyes.mvvm.viewmodel.fragment.OpenEyesMineFragment
import com.fphoenixcorneae.util.ColorUtil
import com.fphoenixcorneae.util.FragmentUtil
import com.fphoenixcorneae.util.ResourceUtil
import kotlinx.android.synthetic.main.open_eyes_activity_home.*

/**
 * @desc 首页 Activity
 */
class OpenEyesHomeActivity :
    OpenEyesBaseDagger2Activity<OpenEyesHomeContract.View, OpenEyesHomePresenter>() {

    private val mFragments = arrayListOf(
        OpenEyesHomeFragment.getInstance(),
        OpenEyesDiscoveryFragment.getInstance(),
        OpenEyesHotFragment.getInstance(),
        OpenEyesMineFragment.getInstance()
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
            .setFont(ResourceUtil.getFont(R.font.open_eyes_fzlt_black_simplified_medium_bold))
            .setTabs(
                listOf(
                    BottomNavigationItem(
                        getString(R.string.open_eyes_home_tab_homepage),
                        ColorUtil.setAlphaComponent(ColorUtil.randomColor, 0.1f),
                        R.drawable.open_eyes_ic_nav_home
                    ),
                    BottomNavigationItem(
                        getString(R.string.open_eyes_home_tab_discover),
                        ColorUtil.setAlphaComponent(ColorUtil.randomColor, 0.1f),
                        R.drawable.open_eyes_ic_nav_discover
                    ),
                    BottomNavigationItem(
                        getString(R.string.open_eyes_home_tab_hot),
                        ColorUtil.setAlphaComponent(ColorUtil.randomColor, 0.1f),
                        R.drawable.open_eyes_ic_nav_hot
                    ),
                    BottomNavigationItem(
                        getString(R.string.open_eyes_home_tab_mine),
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
