package com.wkz.extension

import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager

/**
 * dp转px
 *
 * @param dpValue dp值
 * @return px值
 */
fun Context.dp2px(dpValue: Float): Int {
    val scale = resources.displayMetrics.density
    return (dpValue * scale + 0.5f).toInt()
}

fun Context.dpToPx(dpValue: Float): Float {
    val scale = resources.displayMetrics.density
    return dpValue * scale + 0.5f
}

/**
 * px转dp
 *
 * @param pxValue px值
 * @return dp值
 */
fun Context.px2dp(pxValue: Float): Int {
    val scale = resources.displayMetrics.density
    return (pxValue / scale + 0.5f).toInt()
}

fun Context.pxToDp(pxValue: Float): Float {
    val scale = resources.displayMetrics.density
    return pxValue / scale + 0.5f
}

/**
 * sp转px
 *
 * @param spValue sp值
 * @return px值
 */
fun Context.sp2px(spValue: Float): Int {
    val fontScale = resources.displayMetrics.scaledDensity
    return (spValue * fontScale + 0.5f).toInt()
}

fun Context.spToPx(spValue: Float): Float {
    val fontScale = resources.displayMetrics.scaledDensity
    return spValue * fontScale + 0.5f
}

/**
 * px转sp
 *
 * @param pxValue px值
 * @return sp值
 */
fun Context.px2sp(pxValue: Float): Int {
    val fontScale = resources.displayMetrics.scaledDensity
    return (pxValue / fontScale + 0.5f).toInt()
}

fun Context.pxToSp(pxValue: Float): Float {
    val fontScale = resources.displayMetrics.scaledDensity
    return pxValue / fontScale + 0.5f
}

/**
 * 获取屏幕的宽度（单位：px）
 *
 * @return 屏幕宽px
 */
val Context.getScreenWidth: Int
    get() {
        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val dm = DisplayMetrics()
        windowManager.defaultDisplay?.getMetrics(dm)
        return dm.widthPixels
    }