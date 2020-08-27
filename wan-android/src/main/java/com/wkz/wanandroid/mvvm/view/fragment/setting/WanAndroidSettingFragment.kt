package com.wkz.wanandroid.mvvm.view.fragment.setting

import android.view.View
import com.fphoenixcorneae.animation.attention.Swing
import com.fphoenixcorneae.animation.fade.FadeExit
import com.fphoenixcorneae.dialog.MaterialDialog
import com.wkz.extension.androidViewModel
import com.wkz.extension.navigate
import com.wkz.extension.navigateUp
import com.wkz.extension.visible
import com.wkz.util.ResourceUtil
import com.wkz.wanandroid.R
import com.wkz.wanandroid.manager.WanAndroidUserManager
import com.wkz.wanandroid.mvvm.view.fragment.WanAndroidBaseFragment
import com.wkz.wanandroid.mvvm.viewmodel.WanAndroidAccountViewModel
import kotlinx.android.synthetic.main.wan_android_fragment_setting.*

/**
 * @desc: 设置Fragment
 * @date: 2020-06-04 16:20
 */
class WanAndroidSettingFragment : WanAndroidBaseFragment(), View.OnClickListener {

    /* 账号信息视图模型 */
    private val mAccountViewModel by androidViewModel<WanAndroidAccountViewModel>()

    override fun getLayoutId(): Int = R.layout.wan_android_fragment_setting

    override fun initView() {

    }

    override fun lazyLoadData() {
        // 设置标题栏主题样式
        mTbTitleBar.init()
        mCvLogout.visible(WanAndroidUserManager.sHasLoggedOn)
    }

    override fun isAlreadyLoadedData(): Boolean = true

    override fun initListener() {
        mCvAbout.setOnClickListener(this)
        mBtnLogout.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v) {
            mCvAbout -> navigate(R.id.mSettingToAbout)
            mBtnLogout -> {
                MaterialDialog(mContext)
                    .title(getString(R.string.wan_android_mine_logout))
                    .bgColor(ResourceUtil.getColor(R.color.wan_android_color_white))
                    .content(getString(R.string.wan_android_setting_confirm_logout))
                    .contentTextColor(ResourceUtil.getColor(R.color.wan_android_colorPrimary))
                    .contentTextSize(18f)
                    .btnText(
                        getString(R.string.wan_android_cancel),
                        getString(R.string.wan_android_confirm)
                    )
                    .btnTextColor(
                        ResourceUtil.getColor(R.color.wan_android_color_lighter_gray),
                        ResourceUtil.getColor(R.color.wan_android_colorAccent)
                    )
                    .btnTextSize(16f, 20f)
                    .showAnim(Swing())
                    .dismissAnim(FadeExit())
                    .animatedView(mClRoot)
                    .setOnBtnClickListeners(
                        {
                            it.dismiss()
                        },
                        {
                            // 退出登录
                            logout()
                            it.dismiss()
                        }
                    )
                    .show()
            }
        }
    }

    /**
     * 退出登录
     */
    private fun logout() {
        mAccountViewModel.mLoginSuccess.postValue(false)
        WanAndroidUserManager.logout()

        navigateUp()
    }
}