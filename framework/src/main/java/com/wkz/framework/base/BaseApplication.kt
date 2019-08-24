package com.wkz.framework.base

import android.app.Activity
import android.app.Application
import com.orhanobut.logger.Logger
import com.wkz.util.ContextUtil
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import io.reactivex.plugins.RxJavaPlugins
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

        // RxJava OnErrorNotImplementedException 的处理
        RxJavaPlugins.setErrorHandler {
            Logger.d("onRxJavaErrorHandler ---->: $it")
        }
    }

    override fun activityInjector(): AndroidInjector<Activity> = dispatchingActivityInjector
}