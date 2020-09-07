package com.fphoenixcorneae.util.xtoast.draggable

import android.graphics.Rect
import android.view.Gravity
import android.view.View
import android.view.ViewConfiguration
import android.view.WindowManager
import com.fphoenixcorneae.util.xtoast.XToast
import com.fphoenixcorneae.util.xtoast.listener.OnClickListener

/**
 * 拖拽抽象基类
 */
abstract class BaseDraggable : View.OnTouchListener {
    protected var xToast: XToast? = null
        private set
    protected var rootView: View? = null
        private set
    protected var onClickListener: OnClickListener? = null
        private set
    protected var windowManager: WindowManager? = null
        private set
    protected var windowParams: WindowManager.LayoutParams? = null
        private set
    protected var mTouchSlop: Int = 0
    protected var mWhetherMove: Boolean = false

    /**
     * Toast 显示后回调这个类
     */
    open fun start(toast: XToast) {
        mTouchSlop = ViewConfiguration.get(toast.context).scaledTouchSlop
        xToast = toast
        rootView = toast.view
        onClickListener = toast.onClickListener
        windowManager = toast.windowManager
        windowParams = toast.windowParams
        rootView!!.setOnTouchListener(this)
    }

    /**
     * 获取状态栏的高度
     */
    protected val statusBarHeight: Int
        protected get() {
            val frame = Rect()
            rootView!!.getWindowVisibleDisplayFrame(frame)
            return frame.top
        }

    fun updateLocation(x: Float, y: Float) {
        updateLocation(x.toInt(), y.toInt())
    }

    /**
     * 更新 WindowManager 所在的位置
     */
    fun updateLocation(x: Int, y: Int) {
        if (windowParams!!.x != x || windowParams!!.y != y) {
            windowParams!!.x = x
            windowParams!!.y = y
            // 一定要先设置重心位置为左上角
            windowParams!!.gravity = Gravity.TOP or Gravity.START
            windowManager!!.updateViewLayout(rootView, windowParams)
        }
    }
}