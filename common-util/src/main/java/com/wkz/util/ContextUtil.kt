package com.wkz.util

import android.annotation.SuppressLint
import android.content.Context

/**
 * 上下文工具类,very important：必须首先初始化！
 * 1、在Application的onCreate()中进行初始化！
 * 2、在ContentProvider的onCreate()中进行初始化,然后在AndroidManifest.xml中配置Provider！
 * @author wkz
 * @date 2019/06/20 21:33
 */
class ContextUtil private constructor() {

    init {
        throw UnsupportedOperationException("You can't initialize me...")
    }

    companion object {

        @SuppressLint("StaticFieldLeak")
        private var sContext: Context? = null

        /**
         * 初始化上下文
         *
         * @param context 上下文
         */
        fun init(context: Context) {
            sContext = context.applicationContext
        }

        /**
         * 获取ApplicationContext
         *
         * @return ApplicationContext
         */
        val context: Context
            get() {
                if (sContext == null) {
                    throw NullPointerException("U should call ContextUtil.init(context: Context) first!")
                }
                return sContext as Context
            }

        /**
         * 销毁
         */
        fun dispose() {
            sContext = null
        }
    }
}
