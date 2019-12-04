package com.wkz.util

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

/**
 * 吐司相关工具类
 */
class ToastUtil private constructor() {

    init {
        throw UnsupportedOperationException("U can't initialize me...")
    }

    companion object {

        private var sToast: Toast? = null
        private var gravity = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM
        private var xOffset = 0
        private var yOffset =
            (64 * ContextUtil.context.getResources().getDisplayMetrics().density + 0.5).toInt()
        @SuppressLint("StaticFieldLeak")
        private var customView: View? = null
        private val sHandler = Handler(Looper.getMainLooper())

        /**
         * 设置吐司位置
         *
         * @param gravity 位置
         * @param xOffset x偏移
         * @param yOffset y偏移
         */
        fun setGravity(gravity: Int, xOffset: Int, yOffset: Int) {
            ToastUtil.gravity = gravity
            ToastUtil.xOffset = xOffset
            ToastUtil.yOffset = yOffset
        }

        /**
         * 设置吐司view
         *
         * @param layoutId 视图
         */
        fun setView(layoutId: Int) {
            customView = LayoutInflater.from(ContextUtil.context).inflate(layoutId, null)
        }

        /**
         * 获取吐司view
         *
         * @return view 自定义view
         */
        val view: View?
            get() {
                if (customView != null) {
                    return customView
                }
                return if (sToast != null) {
                    sToast!!.view
                } else null
            }

        /**
         * 设置吐司view
         *
         * @param view 视图
         */
        @JvmStatic
        fun setView(view: View) {
            customView = view
        }

        /**
         * 安全地显示短时吐司
         *
         * @param text 文本
         */
        @JvmStatic
        fun showShortSafe(text: CharSequence) {
            sHandler.post { show(text, Toast.LENGTH_SHORT) }
        }

        /**
         * 安全地显示短时吐司
         *
         * @param resId 资源Id
         */
        @JvmStatic
        fun showShortSafe(resId: Int) {
            sHandler.post { show(resId, Toast.LENGTH_SHORT) }
        }

        /**
         * 安全地显示短时吐司
         *
         * @param resId 资源Id
         * @param args  参数
         */
        @JvmStatic
        fun showShortSafe(resId: Int, vararg args: Any) {
            sHandler.post { show(resId, Toast.LENGTH_SHORT, *args) }
        }

        /**
         * 安全地显示短时吐司
         *
         * @param format 格式
         * @param args   参数
         */
        @JvmStatic
        fun showShortSafe(format: String, vararg args: Any) {
            sHandler.post { show(format, Toast.LENGTH_SHORT, *args) }
        }

        /**
         * 安全地显示长时吐司
         *
         * @param text 文本
         */
        @JvmStatic
        fun showLongSafe(text: CharSequence) {
            sHandler.post { show(text, Toast.LENGTH_LONG) }
        }

        /**
         * 安全地显示长时吐司
         *
         * @param resId 资源Id
         */
        @JvmStatic
        fun showLongSafe(resId: Int) {
            sHandler.post { show(resId, Toast.LENGTH_LONG) }
        }

        /**
         * 安全地显示长时吐司
         *
         * @param resId 资源Id
         * @param args  参数
         */
        @JvmStatic
        fun showLongSafe(resId: Int, vararg args: Any) {
            sHandler.post { show(resId, Toast.LENGTH_LONG, *args) }
        }

        /**
         * 安全地显示长时吐司
         *
         * @param format 格式
         * @param args   参数
         */
        @JvmStatic
        fun showLongSafe(format: String, vararg args: Any) {
            sHandler.post { show(format, Toast.LENGTH_LONG, *args) }
        }

        /**
         * 显示短时吐司
         *
         * @param text 文本
         */
        @JvmStatic
        fun showShort(text: CharSequence) {
            show(text, Toast.LENGTH_SHORT)
        }

        /**
         * 显示短时吐司
         *
         * @param resId 资源Id
         */
        @JvmStatic
        fun showShort(resId: Int) {
            show(resId, Toast.LENGTH_SHORT)
        }

        /**
         * 显示短时吐司
         *
         * @param resId 资源Id
         * @param args  参数
         */
        @JvmStatic
        fun showShort(resId: Int, vararg args: Any) {
            show(resId, Toast.LENGTH_SHORT, *args)
        }

        /**
         * 显示短时吐司
         *
         * @param format 格式
         * @param args   参数
         */
        @JvmStatic
        fun showShort(format: String, vararg args: Any) {
            show(format, Toast.LENGTH_SHORT, *args)
        }

        /**
         * 显示长时吐司
         *
         * @param text 文本
         */
        @JvmStatic
        fun showLong(text: CharSequence) {
            show(text, Toast.LENGTH_LONG)
        }

        /**
         * 显示长时吐司
         *
         * @param resId 资源Id
         */
        @JvmStatic
        fun showLong(resId: Int) {
            show(resId, Toast.LENGTH_LONG)
        }

        /**
         * 显示长时吐司
         *
         * @param resId 资源Id
         * @param args  参数
         */
        @JvmStatic
        fun showLong(resId: Int, vararg args: Any) {
            show(resId, Toast.LENGTH_LONG, *args)
        }

        /**
         * 显示长时吐司
         *
         * @param format 格式
         * @param args   参数
         */
        @JvmStatic
        fun showLong(format: String, vararg args: Any) {
            show(format, Toast.LENGTH_LONG, *args)
        }

        /**
         * 显示吐司
         *
         * @param resId    资源Id
         * @param duration 显示时长
         */
        private fun show(resId: Int, duration: Int) {
            show(ContextUtil.context.getResources().getText(resId).toString(), duration)
        }

        /**
         * 显示吐司
         *
         * @param resId    资源Id
         * @param duration 显示时长
         * @param args     参数
         */
        private fun show(resId: Int, duration: Int, vararg args: Any) {
            show(
                String.format(ContextUtil.context.getResources().getString(resId), *args),
                duration
            )
        }

        /**
         * 显示吐司
         *
         * @param format   格式
         * @param duration 显示时长
         * @param args     参数
         */
        private fun show(format: String, duration: Int, vararg args: Any) {
            show(String.format(format, *args), duration)
        }

        /**
         * 显示吐司
         *
         * @param text     文本
         * @param duration 显示时长
         */
        private fun show(text: CharSequence, duration: Int) {
            cancel()
            if (customView != null) {
                sToast = Toast(ContextUtil.context)
                sToast!!.view = customView
                sToast!!.duration = duration
            } else {
                sToast = getToast(text, duration)
            }
            sToast!!.setGravity(gravity, xOffset, yOffset)
            sToast!!.show()
        }

        /**
         * 取消吐司显示
         */
        private fun cancel() {
            if (sToast != null) {
                sToast!!.cancel()
                sToast = null
            }
        }

        private fun getToast(text: CharSequence, duration: Int): Toast {
            val mToast = Toast(ContextUtil.context)

            val view = LinearLayout(ContextUtil.context)

            val gradientDrawable = GradientDrawable()
            gradientDrawable.shape = GradientDrawable.RECTANGLE
            gradientDrawable.setColor(-0x80000000)
            gradientDrawable.cornerRadius = SizeUtil.dp2px(30f).toFloat()

            view.background = gradientDrawable
            view.minimumWidth = SizeUtil.dp2px(200f)
            view.gravity = Gravity.CENTER
            view.orientation = LinearLayout.VERTICAL
            view.setPadding(
                SizeUtil.dp2px(15f),
                SizeUtil.dp2px(5f),
                SizeUtil.dp2px(15f),
                SizeUtil.dp2px(5f)
            )

            val textView = TextView(ContextUtil.context)
            textView.text = text
            textView.setTextColor(Color.WHITE)
            textView.textSize = 13f
            textView.gravity = Gravity.CENTER

            view.addView(textView)

            mToast.view = view
            mToast.duration = duration
            return mToast
        }
    }
}