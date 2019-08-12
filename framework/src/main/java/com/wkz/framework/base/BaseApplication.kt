package com.wkz.framework.base

import android.app.Activity
import android.app.Application
import com.wkz.util.ContextUtil
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject

/**
 * @desc: 基类 Application
 */
open class BaseApplication : Application(), HasActivityInjector {

    @Inject
    lateinit var dispatchingActivityInjector: DispatchingAndroidInjector<Activity>

    override fun onCreate() {
        super.onCreate()
        // 初始化上下文
        ContextUtil.init(this)
    }

    override fun activityInjector(): AndroidInjector<Activity> = dispatchingActivityInjector
}