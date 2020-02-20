package com.wkz.framework.base

import android.app.Application
import com.orhanobut.logger.Logger
import com.wkz.util.AppUtil
import com.ycbjie.webviewlib.X5WebUtils
import io.reactivex.plugins.RxJavaPlugins

/**
 * @desc: 基类 Application
 */
open class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // RxJava OnErrorNotImplementedException 的处理
        RxJavaPlugins.setErrorHandler {
            Logger.d("onRxJavaErrorHandler ---->: $it")
            // 重新启动App
            AppUtil.relaunchApp()
        }
        // 初始化WebView
        X5WebUtils.init(this)
    }
}