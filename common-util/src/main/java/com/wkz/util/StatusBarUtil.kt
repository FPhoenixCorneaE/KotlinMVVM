package com.wkz.util

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.annotation.ColorInt
import java.util.*

/**
 * 状态栏工具类
 */
object StatusBarUtil {

    @JvmStatic
    fun supportTransparentStatusBar(): Boolean {
        return (RomUtil.isXiaomi
                || RomUtil.isMeizu
                || (RomUtil.isOppo && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                || Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
    }

    /**
     * 设置状态栏透明
     *
     * @param window
     */
    @JvmStatic
    fun transparentStatusBar(window: Window) {
        when {
            RomUtil.isXiaomi || RomUtil.isMeizu -> {
                when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> {
                        transparentStatusBarAbove21(window)
                    }
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
                        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                    }
                }
            }
            RomUtil.isOppo && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> {
                transparentStatusBarAbove21(window)
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                transparentStatusBarAbove21(window)
            }
        }
    }

    @TargetApi(21)
    private fun transparentStatusBarAbove21(window: Window) {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
        window.statusBarColor = Color.TRANSPARENT
    }

    /**
     * 设置状态栏图标白色主题
     *
     * @param window
     */
    @JvmStatic
    fun setLightMode(window: Window) {
        when {
            RomUtil.isXiaomi -> {
                setMIUIStatusBarDarkMode(window, false)
            }
            RomUtil.isMeizu -> {
                setFlymeStatusBarDarkMode(window, false)
            }
            RomUtil.isOppo -> {
                setOppoStatusBarDarkMode(window, false)
            }
            else -> {
                setStatusBarDarkMode(window, false)
            }
        }
    }

    /**
     * 设置状态栏图片黑色主题
     *
     * @param window
     */
    @JvmStatic
    fun setDarkMode(window: Window) {
        when {
            RomUtil.isXiaomi -> {
                setMIUIStatusBarDarkMode(window, true)
            }
            RomUtil.isMeizu -> {
                setFlymeStatusBarDarkMode(window, true)
            }
            RomUtil.isOppo -> {
                setOppoStatusBarDarkMode(window, true)
            }
            else -> {
                setStatusBarDarkMode(window, true)
            }
        }
    }

    private fun setStatusBarDarkMode(
        window: Window,
        darkMode: Boolean
    ) {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                when {
                    darkMode -> {
                        window.decorView.systemUiVisibility =
                            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    }
                    else -> {
                        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    }
                }
            }
        }
    }

    @SuppressLint("PrivateApi")
    private fun setMIUIStatusBarDarkMode(
        window: Window,
        darkMode: Boolean
    ) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            val clazz: Class<out Window> = window.javaClass
            try {
                val layoutParams =
                    Class.forName("android.view.MiuiWindowManager\$LayoutParams")
                val field =
                    layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE")
                val darkModeFlag = field.getInt(layoutParams)
                val extraFlagField = clazz.getMethod(
                    "setExtraFlags",
                    Int::class.javaPrimitiveType,
                    Int::class.javaPrimitiveType
                )
                extraFlagField.invoke(window, if (darkMode) darkModeFlag else 0, darkModeFlag)
            } catch (e: Exception) {
            }
        }
        setStatusBarDarkMode(window, darkMode)
    }

    private fun setFlymeStatusBarDarkMode(
        window: Window,
        darkMode: Boolean
    ) {
        FlymeStatusBarUtil.setStatusBarDarkIcon(window, darkMode)
    }

    private const val SYSTEM_UI_FLAG_OP_STATUS_BAR_TINT = 0x00000010
    private fun setOppoStatusBarDarkMode(
        window: Window,
        darkMode: Boolean
    ) {
        var vis = window.decorView.systemUiVisibility
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                vis = when {
                    darkMode -> {
                        vis or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                    }
                    else -> {
                        vis and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
                    }
                }
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> {
                vis = when {
                    darkMode -> {
                        vis or SYSTEM_UI_FLAG_OP_STATUS_BAR_TINT
                    }
                    else -> {
                        vis and SYSTEM_UI_FLAG_OP_STATUS_BAR_TINT.inv()
                    }
                }
            }
        }
        window.decorView.systemUiVisibility = vis
    }

    /**
     * 设置状态栏颜色和透明度
     *
     * @param window
     * @param color
     * @param alpha
     */
    fun setStatusBarColor(
        window: Window, @ColorInt color: Int,
        alpha: Int
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = calculateStatusColor(color, alpha)
        }
    }

    /**
     * 计算状态栏颜色
     *
     * @param color color值
     * @param alpha alpha值
     * @return 最终的状态栏颜色
     */
    private fun calculateStatusColor(@ColorInt color: Int, alpha: Int): Int {
        if (alpha == 0) {
            return color
        }
        val a = 1 - alpha / 255f
        var red = color shr 16 and 0xff
        var green = color shr 8 and 0xff
        var blue = color and 0xff
        red = (red * a + 0.5).toInt()
        green = (green * a + 0.5).toInt()
        blue = (blue * a + 0.5).toInt()
        return 0xff shl 24 or (red shl 16) or (green shl 8) or blue
    }

    /**
     * 获取状态栏高度
     *
     * @param context
     * @return
     */
    @JvmStatic
    fun getStatusBarHeight(context: Context): Int {
        val resourceId =
            context.resources.getIdentifier("status_bar_height", "dimen", "android")
        return context.resources.getDimensionPixelSize(resourceId)
    }

    /**
     * 获取导航栏高度
     *
     * @param context
     * @return
     */
    fun getNavigationBarHeight(context: Context): Int {
        val resourceId =
            context.resources.getIdentifier("navigation_bar_height", "dimen", "android")
        return context.resources.getDimensionPixelSize(resourceId)
    }

    /**
     * 检测是否有虚拟导航栏
     *
     * @param context
     * @return
     */
    @SuppressLint("PrivateApi")
    fun checkDeviceHasNavigationBar(context: Context): Boolean {
        var hasNavigationBar = false
        val rs = context.resources
        val id = rs.getIdentifier("config_showNavigationBar", "bool", "android")
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id)
        }
        try {
            val systemPropertiesClass =
                Class.forName("android.os.SystemProperties")
            val m =
                systemPropertiesClass.getMethod("get", String::class.java)
            val navBarOverride =
                m.invoke(systemPropertiesClass, "qemu.hw.mainkeys") as String
            if ("1" == navBarOverride) {
                hasNavigationBar = false
            } else if ("0" == navBarOverride) {
                hasNavigationBar = true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return hasNavigationBar
    }

    /**
     * 计算View Id
     *
     * @return
     */
    @JvmStatic
    fun generateViewId(): Int {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 -> {
                View.generateViewId()
            }
            else -> {
                UUID.randomUUID().hashCode()
            }
        }
    }
}