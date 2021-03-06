package com.fphoenixcorneae.wanandroid.mvvm.view.activity

import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import com.fphoenixcorneae.util.ActivityUtil
import com.fphoenixcorneae.util.ContextUtil
import com.fphoenixcorneae.wanandroid.R

/**
 * @desc: 首页Activity
 * @date: 2019-10-28 16:04
 */
class WanAndroidMainActivity : WanAndroidBaseActivity() {

    /**
     *  加载布局
     */
    override fun getLayoutId(): Int = R.layout.wan_android_activity_main

    /**
     * 初始化 View
     */
    override fun initView() {

    }

    override fun initListener() {

    }

    /**
     * 初始化数据
     */
    override fun initData(savedInstanceState: Bundle?) {

    }

    override fun isAlreadyLoadedData(): Boolean = true

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // 应用监听系统DarkMode切换
        when (newConfig.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_NO -> {
                // Night mode is not active, we're using the light theme
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            Configuration.UI_MODE_NIGHT_YES -> {
                // Night mode is active, we're using dark theme
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
        }
        ContextUtil.runOnUiThreadDelayed({
            ActivityUtil.recreate()
        }, 300)
    }
}