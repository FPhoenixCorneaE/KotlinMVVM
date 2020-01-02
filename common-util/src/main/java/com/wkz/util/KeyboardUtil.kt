package com.wkz.util

import android.content.Context
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * 软键盘工具类
 */
object KeyboardUtil {

    /**
     * 打开软键盘
     *
     * @param editText 输入框
     */
    fun openKeyboard(editText: EditText) {
        val imm =
            ContextUtil.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText, InputMethodManager.RESULT_SHOWN)
        imm.toggleSoftInput(
            InputMethodManager.SHOW_FORCED,
            InputMethodManager.HIDE_IMPLICIT_ONLY
        )
    }

    /**
     * 关闭软键盘
     *
     * @param editText 输入框
     */
    fun closeKeyboard(editText: EditText) {
        val imm =
            ContextUtil.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editText.windowToken, 0)
    }

    /**
     * 通过定时器强制隐藏虚拟键盘
     */
    fun timerHideKeyboard(view: View) {
        val scheduledExecutorService =
            Executors.newSingleThreadScheduledExecutor()
        scheduledExecutorService.schedule({
            val imm =
                view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (imm.isActive) {
                imm.hideSoftInputFromWindow(view.applicationWindowToken, 0)
            }
        }, 10, TimeUnit.MILLISECONDS)
    }

    /**
     * 切换软键盘的状态
     * 如当前为收起变为弹出,若当前为弹出变为收起
     */
    fun toggleKeyboard(editText: EditText) {
        val inputMethodManager =
            editText.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(
            0,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }

    /**
     * 强制隐藏输入法键盘
     */
    fun hideKeyboard(editText: EditText) {
        val inputMethodManager =
            editText.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (inputMethodManager.isActive) {
            inputMethodManager.hideSoftInputFromWindow(editText.windowToken, 0)
        }
    }

    /**
     * 强制显示输入法键盘
     */
    fun showKeyboard(editText: EditText) {
        val inputMethodManager =
            editText.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(
            editText,
            InputMethodManager.SHOW_FORCED
        )
    }

    /**
     * 输入法是否显示
     */
    fun keyboardIsShowing(editText: EditText): Boolean {
        val imm =
            editText.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        return imm.isActive
    }

    /**
     * Fix the leaks of soft input.
     *
     * @param window The window.
     */
    fun fixSoftInputLeaks(window: Window) {
        val imm =
            ContextUtil.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val leakViews = arrayOf("mLastSrvView", "mCurRootView", "mServedView", "mNextServedView")
        for (leakView in leakViews) {
            try {
                val leakViewField =
                    InputMethodManager::class.java.getDeclaredField(
                        leakView
                    )
                if (!leakViewField.isAccessible) {
                    leakViewField.isAccessible = true
                }
                val obj = leakViewField[imm] as? View ?: continue
                if (obj.rootView === window.decorView.rootView) {
                    leakViewField[imm] = null
                }
            } catch (ignore: Throwable) {
            }
        }
    }
}