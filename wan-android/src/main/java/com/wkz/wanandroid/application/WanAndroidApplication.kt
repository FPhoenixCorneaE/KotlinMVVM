package com.wkz.wanandroid.application

import androidx.appcompat.app.AppCompatDelegate
import com.wkz.framework.base.BaseApplication

class WanAndroidApplication : BaseApplication() {

    override fun onCreate() {
        super.onCreate()
        // 初始化Night mode,设置Theme随系统变化,应用的Theme继承DayNight Theme
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }
}