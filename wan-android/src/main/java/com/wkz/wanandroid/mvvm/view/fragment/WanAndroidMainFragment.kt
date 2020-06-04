package com.wkz.wanandroid.mvvm.view.fragment

import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.wkz.framework.base.fragment.BaseFragment
import com.wkz.util.FragmentUtil
import com.wkz.wanandroid.R
import kotlinx.android.synthetic.main.wan_android_fragment_main.*

/**
 * @desc: 首页Fragment
 * @date: 2020-06-02 16:55
 */
class WanAndroidMainFragment : BaseFragment() {

    private val mFragments by lazy {
        listOf(
            WanAndroidHomeFragment.getInstance(),
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
            this.isUserInputEnabled = isUserInputEnabled
            // 设置适配器
            adapter = object : FragmentStateAdapter(this@WanAndroidMainFragment) {
                override fun createFragment(position: Int) = mFragments[position]
                override fun getItemCount() = mFragments.size
            }
        }
        onNavigationItemSelected(mBnvMain.menu.getItem(0))
    }

    override fun lazyLoadData() {
    }

    override fun isAlreadyLoadedData(): Boolean = true

    override fun initListener() {
        mBnvMain.setOnNavigationItemSelectedListener {
            onNavigationItemSelected(it)
            return@setOnNavigationItemSelectedListener true
        }
    }

    private fun onNavigationItemSelected(it: MenuItem) {
        when (it.itemId) {
            R.id.mHomeMenuMain -> mVpMain.setCurrentItem(0, false)
            R.id.mHomeMenuProject -> {

            }
            R.id.mHomeMenuSquare -> {

            }
            R.id.mHomeMenuVipcn -> {

            }
            R.id.mHomeMenuMine -> mVpMain.setCurrentItem(1, false)
        }
    }
}