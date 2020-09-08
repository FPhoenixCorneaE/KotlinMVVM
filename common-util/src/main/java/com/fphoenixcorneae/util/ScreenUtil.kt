package com.fphoenixcorneae.util

import android.Manifest
import android.app.Activity
import android.app.KeyguardManager
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Rect
import android.provider.Settings
import android.util.DisplayMetrics
import android.view.Surface
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.annotation.RequiresPermission
import com.fphoenixcorneae.ext.loggerE

/**
 * 屏幕相关工具类
 *
 */
class ScreenUtil private constructor() {

    init {
        throw UnsupportedOperationException("U can't initialize me...")
    }

    companion object {

        /**
         * 获取屏幕的宽度（单位：px）
         *
         * @return 屏幕宽px
         */
        // 创建了一张白纸
        // 给白纸设置宽高
        val screenWidth: Int
            get() {
                val windowManager =
                    ContextUtil.context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                val dm = DisplayMetrics()
                windowManager.defaultDisplay?.getMetrics(dm)
                return dm.widthPixels
            }

        /**
         * 获取屏幕的高度（单位：px）
         *
         * @return 屏幕高px
         */
        // 创建了一张白纸
        // 给白纸设置宽高
        val screenHeight: Int
            get() {
                val windowManager =
                    ContextUtil.context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                val dm = DisplayMetrics()
                windowManager.defaultDisplay?.getMetrics(dm)
                return dm.heightPixels
            }

        /**
         * 判断是否横屏
         *
         * @return `true`: 是<br></br>`false`: 否
         */
        val isLandscape: Boolean
            get() = ContextUtil.context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        /**
         * 设置屏幕为横屏
         *
         * 还有一种就是在Activity中加属性android:screenOrientation="landscape"
         *
         * 不设置Activity的android:configChanges时，切屏会重新调用各个生命周期，切横屏时会执行一次，切竖屏时会执行两次
         *
         * 设置Activity的android:configChanges="orientation"时，切屏还是会重新调用各个生命周期，切横、竖屏时只会执行一次
         *
         * 设置Activity的android:configChanges="orientation|keyboardHidden|screenSize"（4.0以上必须带最后一个参数）时
         * 切屏不会重新调用各个生命周期，只会执行onConfigurationChanged方法
         *
         * @param activity activity
         */
        fun setLandscape(activity: Activity) {
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }

        /**
         * 判断是否竖屏
         *
         * @return `true`: 是<br></br>`false`: 否
         */
        val isPortrait: Boolean
            get() = ContextUtil.context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT

        /**
         * 设置屏幕为竖屏
         *
         * @param activity activity
         */
        fun setPortrait(activity: Activity) {
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }

        /**
         * 获取屏幕旋转角度
         *
         * @param activity activity
         * @return 屏幕旋转角度
         */
        fun getScreenRotation(activity: Activity): Int {
            return when (activity.windowManager.defaultDisplay.rotation) {
                Surface.ROTATION_0 -> 0
                Surface.ROTATION_90 -> 90
                Surface.ROTATION_180 -> 180
                Surface.ROTATION_270 -> 270
                else -> 0
            }
        }

        /**
         * 获取当前屏幕截图，包含状态栏
         *
         * @param activity activity
         * @return Bitmap
         */
        fun captureWithStatusBar(activity: Activity): Bitmap {
            val view = activity.window.decorView
            view.isDrawingCacheEnabled = true
            view.buildDrawingCache()
            val bmp = view.drawingCache
            val dm = DisplayMetrics()
            activity.windowManager.defaultDisplay.getMetrics(dm)
            val ret = Bitmap.createBitmap(bmp, 0, 0, dm.widthPixels, dm.heightPixels)
            view.destroyDrawingCache()
            return ret
        }

        /**
         * 判断是否锁屏
         *
         * @return `true`: 是<br></br>`false`: 否
         */
        val isScreenLock: Boolean
            get() {
                val km =
                    ContextUtil.context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
                return km.inKeyguardRestrictedInputMode()
            }

        /**
         * 获取进入休眠时长
         *
         * @return 进入休眠时长，报错返回-123
         */
        /**
         * 设置进入休眠时长
         *
         * 需添加权限 `<uses-permission android:name="android.permission.WRITE_SETTINGS" />`
         *
         * @param duration 时长
         */
        @get:RequiresPermission(Manifest.permission.WRITE_SETTINGS)
        var sleepDuration: Int
            get() {
                return try {
                    Settings.System.getInt(
                        ContextUtil.context.contentResolver,
                        Settings.System.SCREEN_OFF_TIMEOUT
                    )
                } catch (e: Settings.SettingNotFoundException) {
                    e.printStackTrace()
                    -123
                }

            }
            set(duration) {
                Settings.System.putInt(
                    ContextUtil.context.contentResolver,
                    Settings.System.SCREEN_OFF_TIMEOUT,
                    duration
                )
            }

        /**
         * Get screen density, the logical density of the display
         */
        val screenDensity: Float
            get() = ContextUtil.context.resources.displayMetrics.density

        /**
         * Get screen density dpi, the screen density expressed as dots-per-inch
         */
        val screenDensityDpi: Int
            get() = ContextUtil.context.resources.displayMetrics.densityDpi

        /**
         * Get titlebar height, this method cannot be used in onCreate(),onStart(),onResume(), unless it is called in the
         * post(Runnable).
         */
        fun getTitleBarHeight(activity: Activity): Int {
            val statusBarHeight = getStatusBarHeight(activity)
            val contentViewTop = activity.window.findViewById<View>(Window.ID_ANDROID_CONTENT).top
            val titleBarHeight = contentViewTop - statusBarHeight
            return if (titleBarHeight < 0) 0 else titleBarHeight
        }

        /**
         * Get statusbar height, this method cannot be used in onCreate(),onStart(),onResume(), unless it is called in the
         * post(Runnable).
         */
        fun getStatusBarHeight(activity: Activity): Int {
            val rect = Rect()
            activity.window.decorView.getWindowVisibleDisplayFrame(rect)
            return rect.top
        }

        /**
         * Get statusbar height
         */
        fun getStatusBarHeight2(activity: Activity): Int {
            var statusBarHeight = getStatusBarHeight(activity)
            if (0 == statusBarHeight) {
                val localClass: Class<*>
                try {
                    localClass = Class.forName("com.android.internal.R\$dimen")
                    val localObject = localClass.newInstance()
                    val id =
                        Integer.parseInt(localClass.getField("status_bar_height").get(localObject)!!.toString())
                    statusBarHeight = activity.resources.getDimensionPixelSize(id)
                } catch (e: Exception) {
                    loggerE(e.toString())
                }

            }
            return statusBarHeight
        }
    }
}