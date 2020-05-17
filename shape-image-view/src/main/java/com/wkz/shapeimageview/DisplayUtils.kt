package com.wkz.shapeimageview

import android.content.Context

/**
 * @author Administrator
 */
object DisplayUtils {
    /**
     * 根据手机的分辨率将dp的单位转成px(像素)
     *
     * @param context 上下文
     * @param dpValue dp值
     * @return px值
     */
    fun dip2px(context: Context, dpValue: Float): Float {
        val scale = context.resources.displayMetrics.density
        return dpValue * scale + 0.5f
    }

    /**
     * 根据手机的分辨率将px(像素)的单位转成dp
     *
     * @param context 上下文
     * @param pxValue px值
     * @return dp值
     */
    fun px2dip(context: Context, pxValue: Float): Float {
        val scale = context.resources.displayMetrics.density
        return pxValue / scale + 0.5f
    }

    /**
     * 将px值转换为sp值
     *
     * @param context 上下文
     * @param pxValue px值
     * @return sp值
     */
    fun px2sp(context: Context, pxValue: Float): Int {
        val fontScale = context.resources.displayMetrics.scaledDensity
        return (pxValue / fontScale + 0.5f).toInt()
    }

    /**
     * 将sp值转换为px值
     *
     * @param context 上下文
     * @param spValue sp值
     * @return px值
     */
    fun sp2px(context: Context, spValue: Float): Int {
        val fontScale = context.resources.displayMetrics.scaledDensity
        return (spValue * fontScale + 0.5f).toInt()
    }
}