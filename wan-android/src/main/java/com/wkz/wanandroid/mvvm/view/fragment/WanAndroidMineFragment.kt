package com.wkz.wanandroid.mvvm.view.fragment

import android.view.View
import android.view.animation.LinearInterpolator
import androidx.lifecycle.Observer
import com.bumptech.glide.GenericTransitionOptions
import com.wkz.animation_dsl.animSet
import com.wkz.extension.*
import com.wkz.util.ResourceUtil
import com.wkz.wanandroid.R
import com.wkz.wanandroid.manager.WanAndroidUserManager
import com.wkz.wanandroid.mvvm.view.activity.WanAndroidBaseFragment
import com.wkz.wanandroid.mvvm.viewmodel.WanAndroidAccountViewModel
import com.wkz.wanandroid.mvvm.viewmodel.WanAndroidMineIntegralViewModel
import kotlinx.android.synthetic.main.wan_android_fragment_mine.*

/**
 * @desc：我的Fragment
 * @date：2020-04-26 12:53
 */
class WanAndroidMineFragment : WanAndroidBaseFragment() {

    /* 账号信息视图模型 */
    private val mAccountViewModel by androidViewModel<WanAndroidAccountViewModel>()

    /* 积分视图模型 */
    private val mMineIntegralViewModel by viewModel<WanAndroidMineIntegralViewModel>()

    override fun getLayoutId(): Int = R.layout.wan_android_fragment_mine

    override fun initView() {

    }

    override fun initListener() {
        mClUserInfo.setOnClickListener {
            when {
                !WanAndroidUserManager.sHasLoggedOn -> {
                    // 未登录
                    goToLoginUI()
                }
            }
        }
        mCvIntegral.setOnClickListener {
            when {
                WanAndroidUserManager.sHasLoggedOn -> {

                }
                else -> {
                    // 未登录
                    goToLoginUI()
                }
            }
        }
        mCvCollect.setOnClickListener {
            when {
                WanAndroidUserManager.sHasLoggedOn -> {

                }
                else -> {
                    // 未登录
                    goToLoginUI()
                }
            }
        }
        mCvArticle.setOnClickListener {
            when {
                WanAndroidUserManager.sHasLoggedOn -> {

                }
                else -> {
                    // 未登录
                    goToLoginUI()
                }
            }
        }
        mCvToDoList.setOnClickListener {
            when {
                WanAndroidUserManager.sHasLoggedOn -> {

                }
                else -> {
                    // 未登录
                    goToLoginUI()
                }
            }
        }
        mCvWebsite.setOnClickListener {
        }
        mCvJoin.setOnClickListener {
        }
        mCvSetting.setOnClickListener {
            goToSettingUI()
        }
        mAccountViewModel.apply {
            // 登录成功
            mLoginSuccess.observe(viewLifecycleOwner, Observer {
                if (it) {
                    // 获取积分
                    mMineIntegralViewModel.getIntegral()
                } else {
                    // 退出登录
                    mTvUserName.text =
                        ResourceUtil.getString(R.string.wan_android_mine_user_has_not_log_in)
                    mTvUserId.text = "id：--"
                    mTvUserRanking.text = "排名：--"
                    mTvCurrentIntegral.gone()
                }
            })
        }
        mMineIntegralViewModel.apply {
            // 用户积分
            mUserIntegral.observe(viewLifecycleOwner, Observer {
                it.apply {
                    mTvUserName.text = username
                    mTvUserId.text = "id：${userId}"
                    mTvUserRanking.text = "排名：${rank}"
                    mTvCurrentIntegral.visible()
                    mTvCurrentIntegral.text = "当前积分：$coinCount"
                }
            })
        }
    }

    /**
     * 跳转到登录界面
     */
    private fun goToLoginUI() {
        navigate(R.id.mMainToLogin)
    }

    /**
     * 跳转到设置界面
     */
    private fun goToSettingUI() {
        navigate(R.id.mMainToSetting)
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
    }

    override fun isAlreadyLoadedData(): Boolean = true

    companion object {
        fun getInstance(): WanAndroidMineFragment {
            return WanAndroidMineFragment()
        }
    }
}