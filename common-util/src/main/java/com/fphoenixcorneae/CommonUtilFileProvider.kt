package com.fphoenixcorneae

import androidx.core.content.FileProvider
import com.fphoenixcorneae.ext.loggerD
import com.fphoenixcorneae.util.AppUtil
import com.fphoenixcorneae.util.BuildConfig
import com.fphoenixcorneae.util.ContextUtil
import com.fphoenixcorneae.util.CrashUtil
import com.fphoenixcorneae.util.toast.ToastUtil
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy

/**
 * 工具集文件提供者:
 * 1、初始化ContextUtil;
 * 2、初始化ToastUtil;
 * 3、初始化日志打印配置;
 */
class CommonUtilFileProvider : FileProvider() {

    override fun onCreate(): Boolean {
        // 初始化ContextUtil
        ContextUtil.init(context!!)
        // 初始化ToastUtil
        ToastUtil.init(ContextUtil.context)
        // 初始化CrashUtil
        initCrashUtil()
        // 初始化日志打印配置
        initLoggerConfig()

        // 返回true表示初始化成功,返回false则初始化失败.
        return true
    }

    /**
     * 初始化崩溃处理工具
     */
    private fun initCrashUtil() {
        CrashUtil.init(object : CrashUtil.OnCrashListener {
            override fun onCrash(crashInfo: String, e: Throwable?) {
                loggerD(crashInfo)
                // 重启应用
                AppUtil.relaunchApp()
            }
        })
    }

    /**
     * 初始化日志打印配置
     */
    private fun initLoggerConfig() {
        val formatStrategy = PrettyFormatStrategy.newBuilder()
            // 隐藏线程信息 默认：显示
            .showThreadInfo(false)
            // 决定打印多少行（每一行代表一个方法）默认：2
            .methodCount(2)
            // (Optional) Hides internal method calls up to offset. Default 5
            .methodOffset(7)
            // (Optional) Global tag for every log. Default PRETTY_LOGGER
            .tag("KotlinMVVM")
            .build()
        Logger.addLogAdapter(object : AndroidLogAdapter(formatStrategy) {
            override fun isLoggable(priority: Int, tag: String?): Boolean {
                return BuildConfig.DEBUG
            }
        })
    }
}