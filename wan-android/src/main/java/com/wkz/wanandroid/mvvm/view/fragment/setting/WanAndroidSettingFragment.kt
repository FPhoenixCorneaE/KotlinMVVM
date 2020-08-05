package com.wkz.wanandroid.mvvm.view.fragment.setting

import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.actions.getActionButton
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.wkz.extension.androidViewModel
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
class WanAndroidSettingFragment : WanAndroidBaseFragment() {

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
        mBtnLogout.setOnClickListener {
            MaterialDialog(mContext).show {
                title(text = "退出登录")
                message(text = "确定退出登录吗？")
                positiveButton(text = "确定") {
                    // 退出登录
                    logout()
                }
                negativeButton(text = "取消") {

                }
                lifecycleOwner(viewLifecycleOwner)
                view.titleLayout.findViewById<TextView>(R.id.md_text_title)
                    .setTextColor(ResourceUtil.getColor(R.color.wan_android_colorAccent))
                getActionButton(WhichButton.POSITIVE)
                    .updateTextColor(ResourceUtil.getColor(R.color.wan_android_colorAccent))
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