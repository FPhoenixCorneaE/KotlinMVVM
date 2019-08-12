package com.wkz.kotlinmvvm.application

import android.app.Activity
import android.os.Bundle
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import com.wkz.framework.base.BaseApplication
import com.wkz.kotlinmvvm.BuildConfig
import com.wkz.kotlinmvvm.dagger2.DaggerOpenEyesAppComponent

class OpenEyesApplication : BaseApplication() {

    override fun onCreate() {
        super.onCreate()
        // 初始化日志打印配置
        initLoggerConfig()

        // 注册应用生命周期回调
        registerActivityLifecycleCallbacks(mActivityLifecycleCallbacks)

        // Dagger2注入依赖
        DaggerOpenEyesAppComponent.builder().create(this).inject(this)
    }

    /**
     * 初始化日志打印配置
     */
    private fun initLoggerConfig() {
        val formatStrategy = PrettyFormatStrategy.newBuilder()
            // 隐藏线程信息 默认：显示
            .showThreadInfo(false)
            // 决定打印多少行（每一行代表一个方法）默认：2
            .methodCount(0)
            // (Optional) Hides internal method calls up to offset. Default 5
            .methodOffset(7)
            // (Optional) Global tag for every log. Default PRETTY_LOGGER
            .tag("OpenEyes")
            .build()
        Logger.addLogAdapter(object : AndroidLogAdapter(formatStrategy) {
            override fun isLoggable(priority: Int, tag: String?): Boolean {
                return BuildConfig.DEBUG
            }
        })
    }

    /**
     * 每一个Activity的生命周期都会回调到这里的对应方法。
     * 可以统计Activity的个数等
     */
    private val mActivityLifecycleCallbacks = object : ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            logActivityLifecycle("onCreated(): ", activity)
        }

        override fun onActivityStarted(activity: Activity) {
            logActivityLifecycle("onStarted(): ", activity)
        }

        override fun onActivityResumed(activity: Activity) {
            logActivityLifecycle("onResumed(): ", activity)
        }

        override fun onActivityPaused(activity: Activity) {
            logActivityLifecycle("onPaused(): ", activity)
        }

        override fun onActivityStopped(activity: Activity) {
            logActivityLifecycle("onStopped(): ", activity)
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
            logActivityLifecycle("onSaveInstanceState(): ", activity)
        }

        override fun onActivityDestroyed(activity: Activity) {
            logActivityLifecycle("onDestroyed(): ", activity)
        }

        private fun logActivityLifecycle(message: String, activity: Activity) {
            Logger.t("ActivityLifecycle").d(message + activity.componentName.className)
        }
    }
}