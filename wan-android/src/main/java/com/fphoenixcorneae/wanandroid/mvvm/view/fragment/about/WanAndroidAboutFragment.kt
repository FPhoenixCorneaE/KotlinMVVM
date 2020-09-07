package com.fphoenixcorneae.wanandroid.mvvm.view.fragment.about

import android.annotation.SuppressLint
import com.fphoenixcorneae.util.AppUtil
import com.fphoenixcorneae.wanandroid.R
import com.fphoenixcorneae.wanandroid.mvvm.view.fragment.WanAndroidBaseFragment
import kotlinx.android.synthetic.main.wan_android_fragment_about.*

/**
 * @desc：关于Fragment
 * @date：2020-08-27 15:11
 */
class WanAndroidAboutFragment : WanAndroidBaseFragment() {

    override fun getLayoutId(): Int = R.layout.wan_android_fragment_about

    override fun initView() {

    }

    @SuppressLint("SetTextI18n")
    override fun lazyLoadData() {
        // 设置标题栏主题样式
        mTbTitleBar.init()
        mTvVersionName.text = "For Android ${AppUtil.versionName}"
    }

    override fun isAlreadyLoadedData(): Boolean = true
}