package com.fphoenixcorneae.wanandroid.mvvm.view.fragment.mine

import android.annotation.SuppressLint
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.lifecycle.Observer
import com.bumptech.glide.GenericTransitionOptions
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import com.fphoenixcorneae.dsl.animation.animSet
import com.fphoenixcorneae.ext.*
import com.fphoenixcorneae.ext.view.gone
import com.fphoenixcorneae.ext.view.visible
import com.fphoenixcorneae.framework.web.BaseWebFragment
import com.fphoenixcorneae.util.AppUtil
import com.fphoenixcorneae.util.BundleBuilder
import com.fphoenixcorneae.util.ResourceUtil
import com.fphoenixcorneae.wanandroid.R
import com.fphoenixcorneae.wanandroid.api.WanAndroidUrlConstant
import com.fphoenixcorneae.wanandroid.manager.WanAndroidUserManager
import com.fphoenixcorneae.wanandroid.mvvm.view.fragment.WanAndroidBaseFragment
import com.fphoenixcorneae.wanandroid.mvvm.viewmodel.WanAndroidAccountViewModel
import com.fphoenixcorneae.wanandroid.mvvm.viewmodel.WanAndroidIntegralViewModel
import kotlinx.android.synthetic.main.wan_android_fragment_mine.*

/**
 * @desc：我的Fragment
 * @date：2020-04-26 12:53
 */
class WanAndroidMineFragment : WanAndroidBaseFragment(), OnRefreshListener {

    /* 账号信息ViewModel */
    private val mAccountViewModel by androidViewModel<WanAndroidAccountViewModel>()

    /* 积分ViewModel */
    private val mMineIntegralViewModel by viewModel<WanAndroidIntegralViewModel>()

    override fun getLayoutId(): Int = R.layout.wan_android_fragment_mine

    override fun initView() {
        mSrlRefresh.setEnableLoadMore(false)
        // 设置级别CompoundDrawables
        val compoundStartDrawable = ResourceUtil.getDrawable(R.drawable.wan_android_ic_level)
        compoundStartDrawable?.setBounds(0, 0, dp2px(24f), dp2px(20f))
        mTvUserLevel.setCompoundDrawables(compoundStartDrawable, null, null, null)
    }

    @SuppressLint("SetTextI18n")
    override fun initListener() {
        mSrlRefresh.setOnRefreshListener(this)
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
                    navigateNext(R.id.integralRankingFragment)
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
                    navigateNext(R.id.collectFragment)
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
            navigateNext(
                R.id.webFragment,
                BundleBuilder.of()
                    .putCharSequence(BaseWebFragment.TITLE, AppUtil.appName)
                    .putString(BaseWebFragment.WEB_URL, WanAndroidUrlConstant.BASE_URL)
                    .get()
            )
        }
        mCvJoin.setOnClickListener {
            joinQQGroup("mBewO84lNNVZMZfPpXaF3ZyhGiPHgvdt")
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
                    mTvUserLevel.gone()
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
                    mTvUserLevel.visible()
                    mTvUserLevel.text = level.toString()
                }
            })
        }
    }

    /**
     * 跳转到登录界面
     */
    private fun goToLoginUI() {
        navigateNext(R.id.loginFragment)
    }

    /**
     * 跳转到设置界面
     */
    private fun goToSettingUI() {
        navigateNext(R.id.settingFragment)
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

    override fun onRefresh(refreshLayout: RefreshLayout) {
        when {
            WanAndroidUserManager.sHasLoggedOn -> {
                // 获取积分
                mMineIntegralViewModel.getIntegral()
            }
        }
        mSrlRefresh.finishRefresh(1500)
    }

    override fun isAlreadyLoadedData(): Boolean = true

    companion object {
        fun getInstance(): WanAndroidMineFragment {
            return WanAndroidMineFragment()
        }
    }
}