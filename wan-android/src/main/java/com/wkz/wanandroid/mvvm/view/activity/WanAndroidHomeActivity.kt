package com.wkz.wanandroid.mvvm.view.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.wkz.framework.base.activity.BaseActivity
import com.wkz.util.FragmentUtil
import com.wkz.wanandroid.R
import com.wkz.wanandroid.mvvm.view.fragment.WanAndroidHomeFragment
import com.wkz.wanandroid.mvvm.view.fragment.WanAndroidMineFragment
import kotlinx.android.synthetic.main.wan_android_activity_home.*

/**
 * @desc: 首页Activity
 * @date: 2019-10-28 16:04
 */
class WanAndroidHomeActivity : BaseActivity() {

    private val mHomeFragment by lazy {
        WanAndroidHomeFragment.getInstance()
    }
    private val mMineFragment by lazy {
        WanAndroidMineFragment.getInstance()
    }
    private var mPreviousFragment: Fragment? = null

    /**
     *  加载布局
     */
    override fun getLayoutId(): Int = R.layout.wan_android_activity_home

    /**
     * 初始化 View
     */
    override fun initView() {
        onNavigationItemSelected(mBnvHome.menu.getItem(0))
    }

    override fun initListener() {
        mBnvHome.setOnNavigationItemSelectedListener {
            onNavigationItemSelected(it)
            return@setOnNavigationItemSelectedListener true
        }
    }

    private fun onNavigationItemSelected(it: MenuItem) {
        when (it.itemId) {
            R.id.mHomeMenuMain -> {
                if (mPreviousFragment != mHomeFragment) {
                    FragmentUtil.with(mContext)
                        .setContainerViewId(R.id.mFlActivityContainer)
                        .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                        .hideAndShowFragment(mPreviousFragment, mHomeFragment)
                        .commit()
                    mPreviousFragment = mHomeFragment
                }
            }
            R.id.mHomeMenuProject -> {

            }
            R.id.mHomeMenuSquare -> {

            }
            R.id.mHomeMenuVipcn -> {

            }
            R.id.mHomeMenuMine -> {
                if (mPreviousFragment != mMineFragment) {
                    FragmentUtil.with(mContext)
                        .setContainerViewId(R.id.mFlActivityContainer)
                        .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                        .hideAndShowFragment(mPreviousFragment, mMineFragment)
                        .commit()
                    mPreviousFragment = mMineFragment
                }
            }
        }
    }

    /**
     * 初始化数据
     */
    override fun initData(savedInstanceState: Bundle?) {

    }

    override fun isAlreadyLoadedData(): Boolean = true
}