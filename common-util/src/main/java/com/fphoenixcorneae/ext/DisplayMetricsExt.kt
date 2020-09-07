package com.fphoenixcorneae.ext

import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.fragment.app.Fragment

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

fun Fragment.dp2px(dpValue: Float): Int {
    val scale = resources.displayMetrics.density
    return (dpValue * scale + 0.5f).toInt()
}

fun Fragment.dpToPx(dpValue: Float): Float {
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

fun Fragment.px2dp(pxValue: Float): Int {
    val scale = resources.displayMetrics.density
    return (pxValue / scale + 0.5f).toInt()
}

fun Fragment.pxToDp(pxValue: Float): Float {
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

fun Fragment.sp2px(spValue: Float): Int {
    val fontScale = resources.displayMetrics.scaledDensity
    return (spValue * fontScale + 0.5f).toInt()
}

fun Fragment.spToPx(spValue: Float): Float {
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

fun Fragment.px2sp(pxValue: Float): Int {
    val fontScale = resources.displayMetrics.scaledDensity
    return (pxValue / fontScale + 0.5f).toInt()
}

fun Fragment.pxToSp(pxValue: Float): Float {
    val fontScale = resources.displayMetrics.scaledDensity
    return pxValue / fontScale + 0.5f
}

/**
 * 获取屏幕的宽度（单位：px）
 *
 * @return 屏幕宽px
 */
val Context.screenWidth: Int
    get() {
        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val dm = DisplayMetrics()
        windowManager.defaultDisplay?.getMetrics(dm)
        return dm.widthPixels
    }
val Fragment.screenWidth: Int
    get() {
        return requireActivity().screenWidth
    }

/**
 * 获取屏幕的高度（单位：px）
 *
 * @return 屏幕高px
 */
val Context.screenHeight: Int
    get() {
        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val dm = DisplayMetrics()
        windowManager.defaultDisplay?.getMetrics(dm)
        return dm.heightPixels
    }
val Fragment.screenHeight: Int
    get() {
        return requireActivity().screenHeight
    }