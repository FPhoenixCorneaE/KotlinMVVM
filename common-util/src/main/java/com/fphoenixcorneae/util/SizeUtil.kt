package com.fphoenixcorneae.util

import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup

/**
 * 尺寸相关工具类,必须先初始化ContextUtil
 */
class SizeUtil private constructor() {

    init {
        throw UnsupportedOperationException("U can't initialize me...")
    }

    /**
     * 获取到View尺寸的监听
     */
    interface OnGetSizeListener {
        fun onGetSize(view: View)
    }

    companion object {

        /**
         * dp转px
         *
         * @param dpValue dp值
         * @return px值
         */
        fun dp2px(dpValue: Float): Int {
            val scale = ContextUtil.context.resources.displayMetrics.density
            return (dpValue * scale + 0.5f).toInt()
        }

        /**
         * dp转px
         *
         * @param dpValue dp值
         * @return px值
         */
        fun dpToPx(dpValue: Float): Float {
            val scale = ContextUtil.context.resources.displayMetrics.density
            return dpValue * scale + 0.5f
        }

        /**
         * px转dp
         *
         * @param pxValue px值
         * @return dp值
         */
        fun px2dp(pxValue: Float): Int {
            val scale = ContextUtil.context.resources.displayMetrics.density
            return (pxValue / scale + 0.5f).toInt()
        }

        /**
         * px转dp
         *
         * @param pxValue px值
         * @return dp值
         */
        fun pxToDp(pxValue: Float): Float {
            val scale = ContextUtil.context.resources.displayMetrics.density
            return pxValue / scale + 0.5f
        }

        /**
         * 不同屏幕尺寸的屏幕px转px
         * @param screenSize 屏幕尺寸
         * @param pxValue    screenSize屏幕下px值
         * @return px值
         */
        fun pxToPx(pxValue: Float, screenSize: Int): Float {
            val screenWidth = ContextUtil.context.resources.displayMetrics.widthPixels
            return screenWidth * pxValue / screenSize
        }

        /**
         * sp转px
         *
         * @param spValue sp值
         * @return px值
         */
        fun sp2px(spValue: Float): Int {
            val fontScale = ContextUtil.context.resources.displayMetrics.scaledDensity
            return (spValue * fontScale + 0.5f).toInt()
        }

        /**
         * sp转px
         *
         * @param spValue sp值
         * @return px值
         */
        fun spToPx(spValue: Float): Float {
            val fontScale = ContextUtil.context.resources.displayMetrics.scaledDensity
            return spValue * fontScale + 0.5f
        }

        /**
         * px转sp
         *
         * @param pxValue px值
         * @return sp值
         */
        fun px2sp(pxValue: Float): Int {
            val fontScale = ContextUtil.context.resources.displayMetrics.scaledDensity
            return (pxValue / fontScale + 0.5f).toInt()
        }

        /**
         * px转sp
         *
         * @param pxValue px值
         * @return sp值
         */
        fun pxToSp(pxValue: Float): Float {
            val fontScale = ContextUtil.context.resources.displayMetrics.scaledDensity
            return pxValue / fontScale + 0.5f
        }

        /**
         * 各种单位转换
         *
         * 该方法存在于TypedValue
         *
         * @param unit    单位
         * @param value   值
         * @param metrics DisplayMetrics
         * @return 转换结果
         */
        fun applyDimension(unit: Int, value: Float, metrics: DisplayMetrics): Float {
            when (unit) {
                TypedValue.COMPLEX_UNIT_PX -> return value
                TypedValue.COMPLEX_UNIT_DIP -> return value * metrics.density
                TypedValue.COMPLEX_UNIT_SP -> return value * metrics.scaledDensity
                TypedValue.COMPLEX_UNIT_PT -> return value * metrics.xdpi * (1.0f / 72)
                TypedValue.COMPLEX_UNIT_IN -> return value * metrics.xdpi
                TypedValue.COMPLEX_UNIT_MM -> return value * metrics.xdpi * (1.0f / 25.4f)
            }
            return 0f
        }

        /**
         * 在onCreate中获取视图的尺寸
         *
         * 需回调onGetSizeListener接口，在onGetSize中获取view宽高
         *
         * 用法示例如下所示
         * <pre>
         * SizeUtil.forceGetViewSize(view, new SizeUtil.onGetSizeListener() {
         * Override
         * public void onGetSize(View view) {
         * view.getWidth();
         * }
         * });
        </pre> *
         *
         * @param view     视图
         * @param listener 监听器
         */
        fun forceGetViewSize(view: View, listener: OnGetSizeListener?) {
            view.post {
                listener?.onGetSize(view)
            }
        }

        /**
         * 测量视图尺寸
         *
         * @param view 视图
         * @return arr[0]: 视图宽度, arr[1]: 视图高度
         */
        fun measureView(view: View): IntArray {
            var lp: ViewGroup.LayoutParams? = view.layoutParams
            if (lp == null) {
                lp = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }
            val widthSpec = ViewGroup.getChildMeasureSpec(0, 0, lp.width)
            val lpHeight = lp.height
            val heightSpec: Int
            heightSpec = when {
                lpHeight > 0 -> {
                    View.MeasureSpec.makeMeasureSpec(lpHeight, View.MeasureSpec.EXACTLY)
                }
                else -> {
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                }
            }
            view.measure(widthSpec, heightSpec)
            return intArrayOf(view.measuredWidth, view.measuredHeight)
        }

        /**
         * 获取测量视图宽度
         *
         * @param view 视图
         * @return 视图宽度
         */
        fun getMeasuredWidth(view: View): Int {
            return measureView(view)[0]
        }

        /**
         * 获取测量视图高度
         *
         * @param view 视图
         * @return 视图高度
         */
        fun getMeasuredHeight(view: View): Int {
            return measureView(view)[1]
        }
    }
}
