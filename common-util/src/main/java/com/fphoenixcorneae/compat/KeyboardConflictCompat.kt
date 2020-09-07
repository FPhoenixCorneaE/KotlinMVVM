package com.fphoenixcorneae.compat

import android.R
import android.graphics.Rect
import android.os.Build
import android.view.View
import android.view.Window
import android.widget.FrameLayout
import com.fphoenixcorneae.util.statusbar.StatusBarUtil

/**
 * 解决沉浸式标题栏下，键盘兼容问题
 */
class KeyboardConflictCompat private constructor(window: Window) {
    private val mChildOfContent: View
    private val frameLayoutParams: FrameLayout.LayoutParams
    private var usableHeightPrevious = 0
    private var contentHeight = 0
    private var isFirst = true
    private val statusBarHeight: Int
    private fun possiblyResizeChildOfContent() {
        val usableHeightNow = computeUsableHeight()
        if (usableHeightNow != usableHeightPrevious) {
            val usableHeightSansKeyboard = mChildOfContent.rootView.height
            val heightDifference = usableHeightSansKeyboard - usableHeightNow
            if (heightDifference > usableHeightSansKeyboard / 4) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    frameLayoutParams.height =
                        usableHeightSansKeyboard - heightDifference + statusBarHeight
                } else {
                    frameLayoutParams.height = usableHeightSansKeyboard - heightDifference
                }
            } else {
                frameLayoutParams.height = contentHeight
            }
            // 重绘Activity的xml布局
            mChildOfContent.requestLayout()
            usableHeightPrevious = usableHeightNow
        }
    }

    private fun computeUsableHeight(): Int {
        val r = Rect()
        mChildOfContent.getWindowVisibleDisplayFrame(r)
        // 全屏模式下：直接返回r.bottom，r.top其实是状态栏的高度
        return r.bottom - r.top
    }

    companion object {
        fun assistWindow(window: Window) {
            KeyboardConflictCompat(window)
        }
    }

    init {
        val content =
            window.findViewById<View>(R.id.content) as FrameLayout
        mChildOfContent = content.getChildAt(0)
        mChildOfContent.viewTreeObserver
            .addOnGlobalLayoutListener {
                if (isFirst) {
                    // 兼容华为等机型
                    contentHeight = mChildOfContent.height
                    isFirst = false
                }
                possiblyResizeChildOfContent()
            }
        frameLayoutParams = mChildOfContent.layoutParams as FrameLayout.LayoutParams
        statusBarHeight = StatusBarUtil.getStatusBarHeight(window.context)
    }
}