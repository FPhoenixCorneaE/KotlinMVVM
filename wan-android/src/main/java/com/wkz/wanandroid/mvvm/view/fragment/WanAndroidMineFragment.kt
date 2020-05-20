package com.wkz.wanandroid.mvvm.view.fragment

import android.view.View
import android.view.animation.LinearInterpolator
import androidx.lifecycle.Observer
import com.bumptech.glide.GenericTransitionOptions
import com.wkz.animation_dsl.animSet
import com.wkz.extension.viewModel
import com.wkz.framework.base.fragment.BaseFragment
import com.wkz.wanandroid.R
import com.wkz.wanandroid.manager.WanAndroidUserManager
import com.wkz.wanandroid.mvvm.viewmodel.WanAndroidMineIntegralViewModel
import kotlinx.android.synthetic.main.wan_android_fragment_mine.*

/**
 * @desc：我的Fragment
 * @date：2020-04-26 12:53
 */
class WanAndroidMineFragment : BaseFragment() {

    /* 积分视图模型 */
    private val mMineIntegralViewModel by viewModel<WanAndroidMineIntegralViewModel>()

    /* 是否已登录 */
    private val mHasLoggedOn = WanAndroidUserManager.sHasLoggedOn

    override fun getLayoutId(): Int = R.layout.wan_android_fragment_mine

    override fun initView() {

    }

    override fun initListener() {
        mMineIntegralViewModel.apply {
            mUserIntegral.observe(viewLifecycleOwner, Observer {
                it.apply {
                    mTvUserName.text = username
                    mTvUserId.text = "id ：${userId}"
                    mTvUserRanking.text = "排名 ： ${rank}"
                }
            })
        }
    }

    override fun lazyLoadData() {
        mIvUserAvatar.load(
            R.mipmap.wan_android_ic_avatar_default,
            GenericTransitionOptions.with { view: View ->
                // 设置加载动画
                animSet {
                    objectAnim {
                        target = view
                        alpha = floatArrayOf(0.5f, 1f)
                        scaleX = floatArrayOf(1.3f, 1f)
                        scaleY = floatArrayOf(1.3f, 1f)
                        duration = 400L
                        interpolator = LinearInterpolator()
                    }
                    start()
                }
            }
        )
        when {
            mHasLoggedOn -> {
                // 获取积分
                mMineIntegralViewModel.getIntegral()
            }
        }
    }

    override fun isAlreadyLoadedData(): Boolean = true

    companion object {
        fun getInstance(): WanAndroidMineFragment {
            return WanAndroidMineFragment()
        }
    }
}