package com.fphoenixcorneae.framework.base

import android.app.Application
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import com.fphoenixcorneae.ext.loggerD
import com.fphoenixcorneae.util.AppUtil
import com.fphoenixcorneae.util.DynamicTimeFormat
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import io.reactivex.plugins.RxJavaPlugins
import skin.support.SkinCompatManager
import skin.support.app.SkinCardViewInflater
import skin.support.constraint.app.SkinConstraintViewInflater
import skin.support.design.app.SkinMaterialViewInflater


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
            loggerD("onRxJavaErrorHandler ---->: $it")
            // 重新启动App
            AppUtil.relaunchApp()
        }

        // 初始化SmartRefreshLayout默认设置
        initSmartRefreshLayoutDefaultConfig()

        // 初始化 Android 换肤框架
        initAndroidSkinSupport()
    }

    /**
     * 初始化SmartRefreshLayout默认设置
     */
    private fun initSmartRefreshLayoutDefaultConfig() {
        // 设置全局默认配置（优先级最低，会被其他设置覆盖）
        SmartRefreshLayout.setDefaultRefreshInitializer { _, layout -> //全局设置（优先级最低）
            layout.setEnableAutoLoadMore(true)
            layout.setEnableOverScrollDrag(false)
            layout.setEnableOverScrollBounce(true)
            layout.setEnableScrollContentWhenRefreshed(true)
            layout.setEnableLoadMoreWhenContentNotFull(true)
            layout.setEnableFooterFollowWhenNoMoreData(true)
            layout.setFooterMaxDragRate(4.0f)
            layout.setFooterHeight(45f)
        }
        // 全局设置主题颜色（优先级第二低，可以覆盖 DefaultRefreshInitializer 的配置，与下面的ClassicsHeader绑定）
        SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, layout ->
            layout.setEnableHeaderTranslationContent(true)
            ClassicsHeader(context).setTimeFormat(DynamicTimeFormat("更新于 %s"))
        }
        SmartRefreshLayout.setDefaultRefreshFooterCreator { context, _ ->
            ClassicsFooter(context)
        }
    }

    /**
     * 初始化 Android 换肤框架
     */
    private fun initAndroidSkinSupport() {
        SkinCompatManager.withoutActivity(this) // 基础控件换肤初始化
            .addInflater(SkinMaterialViewInflater()) // material design 控件换肤初始化[可选]
            .addInflater(SkinConstraintViewInflater()) // ConstraintLayout 控件换肤初始化[可选]
            .addInflater(SkinCardViewInflater()) // CardView v7 控件换肤初始化[可选]
            .setSkinStatusBarColorEnable(true) // 打开状态栏换肤，默认打开[可选]
            .setSkinWindowBackgroundEnable(true) // 打开windowBackground换肤，默认打开[可选]
            .loadSkin()
    }

    override fun getViewModelStore(): ViewModelStore {
        return mViewModelStore
    }
}