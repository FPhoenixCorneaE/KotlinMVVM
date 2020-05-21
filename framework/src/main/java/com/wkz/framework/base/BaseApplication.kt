package com.wkz.framework.base

import android.app.Application
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import com.orhanobut.logger.Logger
import com.wkz.util.AppUtil
import io.reactivex.plugins.RxJavaPlugins

/**
 * @desc: 基类 Application
 *        ViewModelStoreOwner提供一个很有用的功能--在Activity/fragment中获取Application级别的ViewModel
 */
open class BaseApplication : Application(), ViewModelStoreOwner {

    private lateinit var mViewModelStore: ViewModelStore

    override fun onCreate() {
        super.onCreate()
        mViewModelStore = ViewModelStore()
        // RxJava OnErrorNotImplementedException 的处理
        RxJavaPlugins.setErrorHandler {
            Logger.d("onRxJavaErrorHandler ---->: $it")
            // 重新启动App
            AppUtil.relaunchApp()
        }
    }

    override fun getViewModelStore(): ViewModelStore {
        return mViewModelStore
    }
}