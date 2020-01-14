package com.wkz.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.MeasureSpec
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.PopupWindow
import android.widget.TextView
import java.util.*

/**
 * 视图工具类
 */
class ViewUtil private constructor() {

    companion object {

        /**
         * 生成View Id
         */
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

        /**
         * 设置某个View的margin
         *
         * @param view   需要设置的view
         * @param isDp   需要设置的数值是否为DP
         * @param left   左边距
         * @param top    上边距
         * @param right  右边距
         * @param bottom 下边距
         * @return
         */
        fun setViewMargin(
            view: View?,
            isDp: Boolean,
            left: Float,
            top: Float,
            right: Float,
            bottom: Float
        ): ViewGroup.LayoutParams? {
            if (view == null) {
                return null
            }
            val params = view.layoutParams
            val marginParams: MarginLayoutParams
            // 获取view的margin设置参数
            marginParams = when (params) {
                is MarginLayoutParams -> {
                    params
                }
                else -> {
                    // 不存在时创建一个新的参数
                    MarginLayoutParams(params)
                }
            }
            // 根据DP与PX转换计算值
            val leftPx: Int
            val rightPx: Int
            val topPx: Int
            val bottomPx: Int
            when {
                isDp -> {
                    leftPx = SizeUtil.dp2px(left)
                    topPx = SizeUtil.dp2px(top)
                    rightPx = SizeUtil.dp2px(right)
                    bottomPx = SizeUtil.dp2px(bottom)
                }
                else -> {
                    leftPx = left.toInt()
                    rightPx = right.toInt()
                    topPx = top.toInt()
                    bottomPx = bottom.toInt()
                }
            }
            // 设置margin
            marginParams.setMargins(leftPx, topPx, rightPx, bottomPx)
            view.layoutParams = marginParams
            return marginParams
        }

        /**
         * 设置某个View的padding
         *
         * @param view   需要设置的view
         * @param isDp   需要设置的数值是否为DP
         * @param left   左边距
         * @param top    上边距
         * @param right  右边距
         * @param bottom 下边距
         */
        fun setViewPadding(
            view: View?,
            isDp: Boolean,
            left: Float,
            top: Float,
            right: Float,
            bottom: Float
        ) {
            if (view == null) {
                return
            }
            // 根据DP与PX转换计算值
            val leftPx: Int
            val rightPx: Int
            val topPx: Int
            val bottomPx: Int
            when {
                isDp -> {
                    leftPx = SizeUtil.dp2px(left)
                    topPx = SizeUtil.dp2px(top)
                    rightPx = SizeUtil.dp2px(right)
                    bottomPx = SizeUtil.dp2px(bottom)
                }
                else -> {
                    leftPx = left.toInt()
                    rightPx = right.toInt()
                    topPx = top.toInt()
                    bottomPx = bottom.toInt()
                }
            }
            // 设置padding
            view.setPadding(leftPx, topPx, rightPx, bottomPx)
        }

        /**
         * 把自身从父View中移除
         */
        fun removeSelfFromParent(view: View?) {
            if (view != null) {
                val parent = view.parent
                if (parent is ViewGroup) {
                    parent.removeView(view)
                }
            }
        }

        /**
         * 判断触点是否落在该View上
         */
        fun isTouchInView(ev: MotionEvent, v: View): Boolean {
            val vLoc = IntArray(2)
            v.getLocationOnScreen(vLoc)
            val motionX = ev.rawX
            val motionY = ev.rawY
            return motionX >= vLoc[0] && motionX <= vLoc[0] + v.width && motionY >= vLoc[1] && motionY <= vLoc[1] + v.height
        }

        /**
         * @param view
         * @param isAll
         */
        fun requestLayoutParent(view: View, isAll: Boolean) {
            var parent = view.parent
            while (parent is View) {
                if (!parent.isLayoutRequested()) {
                    parent.requestLayout()
                    if (!isAll) {
                        break
                    }
                }
                parent = parent.getParent()
            }
        }

        /**
         * @param bmp
         * @param scale
         * @return
         */
        fun scaleImage(bmp: Bitmap, scale: Float): Bitmap {
            val bmpWidth = bmp.width
            val bmpHeight = bmp.height
            val matrix = Matrix()
            matrix.postScale(scale, scale)
            return Bitmap.createBitmap(
                bmp,
                0,
                0,
                bmpWidth,
                bmpHeight,
                matrix,
                true
            )
        }

        /**
         * 给TextView设置下划线
         *
         * @param textView
         */
        fun setTextViewUnderLine(textView: TextView) {
            textView.paint.flags = Paint.UNDERLINE_TEXT_FLAG
            textView.paint.isAntiAlias = true
        }

        var popupWindow: PopupWindow? = null
        /**
         * 显示PopupWindow
         *
         * @param context
         * @param resId
         * @param root
         * @param paramsType
         * @return
         */
        fun showPopupWindow(
            context: Context?,
            resId: Int,
            root: View?,
            paramsType: Int
        ): View {
            val popupView: View = LayoutInflater.from(context).inflate(resId, null)
            popupWindow = when (paramsType) {
                1 -> PopupWindow(
                    popupView,
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true
                )
                2 -> PopupWindow(
                    popupView,
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true
                )
                3 -> PopupWindow(
                    popupView,
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, true
                )
                4 -> PopupWindow(
                    popupView,
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true
                )
                else -> PopupWindow(
                    popupView,
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true
                )
            }
            popupWindow!!.isFocusable = true
            popupWindow!!.isOutsideTouchable = true
            popupWindow!!.isTouchable = true
            popupWindow!!.setBackgroundDrawable(BitmapDrawable())
            popupWindow!!.showAsDropDown(root)
            return popupView
        }

        /**
         * 关闭PopupWindow
         */
        fun dismissPopup() {
            if (popupWindow != null && popupWindow!!.isShowing) {
                popupWindow!!.dismiss()
                popupWindow = null
            }
        }

        /**
         * 截图
         *
         * @param v
         * @return
         */
        fun captureView(v: View): Bitmap {
            v.isDrawingCacheEnabled = true
            v.buildDrawingCache()
            return v.drawingCache
        }

        /**
         * 截图
         *
         * @param v
         * @return
         */
        fun createViewBitmap(v: View): Bitmap {
            val bitmap = Bitmap.createBitmap(
                v.width,
                v.height,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            v.draw(canvas)
            return bitmap
        }

        /**
         * 截图
         *
         * @param view
         * @return
         */
        fun convertViewToBitmap(view: View): Bitmap {
            view.measure(
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
            )
            view.layout(0, 0, view.measuredWidth, view.measuredHeight)
            view.buildDrawingCache()
            return view.drawingCache
        }

        /**
         * 获取Activity的截图
         *
         * @param activity
         * @return
         */
        fun getActivityBitmap(activity: Activity): Bitmap {
            val view = activity.window.decorView
                .findViewById<View>(android.R.id.content)
            view.isDrawingCacheEnabled = true
            return view.drawingCache
        }

        /**
         * 获取状态栏高度
         *
         * @param context
         * @return
         */
        fun getStatusBarHeight(context: Context): Int {
            var result = 0
            val resourceId =
                context.resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                result = context.resources.getDimensionPixelSize(resourceId)
            }
            return result
        }

        /**
         * 获取工具栏高度
         *
         * @param context
         * @return
         */
        fun getToolbarHeight(context: Context): Int {
            val styledAttributes =
                context.theme.obtainStyledAttributes(intArrayOf(R.attr.actionBarSize))
            val toolbarHeight = styledAttributes.getDimension(0, 0f).toInt()
            styledAttributes.recycle()
            return toolbarHeight
        }

        /**
         * 获取导航栏高度
         *
         * @param activity
         * @return
         */
        fun getNavigationBarHeight(activity: Activity): Int {
            val resources = activity.resources
            val resourceId =
                resources.getIdentifier("navigation_bar_height", "dimen", "android")
            return if (resourceId > 0) {
                resources.getDimensionPixelSize(resourceId)
            } else 0
        }

        /**
         * 测量view
         *
         * @param view
         */
        fun measureView(view: View) {
            var p = view.layoutParams
            if (p == null) {
                p = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }
            val childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0, p.width)
            val lpHeight = p.height
            val childHeightSpec: Int
            childHeightSpec = if (lpHeight > 0) {
                MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY)
            } else {
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
            }
            view.measure(childWidthSpec, childHeightSpec)
        }

        /**
         * 获取view的宽度
         *
         * @param view
         * @return
         */
        fun getViewWidth(view: View): Int {
            measureView(view)
            return view.measuredWidth
        }

        /**
         * 获取view的高度
         *
         * @param view
         * @return
         */
        fun getViewHeight(view: View): Int {
            measureView(view)
            return view.measuredHeight
        }

        /**
         * 获取view的上下文
         *
         * @param view
         * @return
         */
        fun getActivity(view: View): Activity {
            var context = view.context
            while (context is ContextWrapper) {
                if (context is Activity) {
                    return context
                }
                context = context.baseContext
            }
            throw IllegalStateException("View $view is not attached to an Activity")
        }
    }

    init {
        throw UnsupportedOperationException("cannot be instantiated")
    }
}