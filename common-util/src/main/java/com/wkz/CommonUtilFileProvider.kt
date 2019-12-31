package com.wkz

import androidx.core.content.FileProvider
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import com.wkz.util.BuildConfig
import com.wkz.util.ContextUtil

/**
 * 工具集文件提供者:
 * 1、初始化上下文工具类;
 * 2、初始化日志打印配置;
 */
class CommonUtilFileProvider : FileProvider() {

    override fun onCreate(): Boolean {
        // 初始化上下文
        ContextUtil.init(context!!)
        // 初始化日志打印配置
        initLoggerConfig()
        return true
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