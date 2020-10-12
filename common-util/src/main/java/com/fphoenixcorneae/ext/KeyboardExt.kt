package com.fphoenixcorneae.ext

import android.app.Activity
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.fphoenixcorneae.util.ContextUtil
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * 打开软键盘
 */
fun EditText?.openKeyboard() {
    this?.let {
        it.context.inputMethodManager?.apply {
            showSoftInput(it, InputMethodManager.RESULT_SHOWN)
            toggleSoftInput(
                InputMethodManager.SHOW_FORCED,
                InputMethodManager.HIDE_IMPLICIT_ONLY
            )
        }
    }
}

/**
 * 强制打开软键盘
 */
fun EditText?.openKeyboardForced() {
    this?.let {
        it.context.inputMethodManager?.apply {
            showSoftInput(it, InputMethodManager.SHOW_FORCED)
        }
    }
}

/**
 * 关闭软键盘
 */
fun EditText?.closeKeyboard() {
    this?.let {
        it.context.inputMethodManager?.apply {
            hideSoftInputFromWindow(it.windowToken, 0)
        }
    }
}

/**
 * 关闭软键盘
 */
fun Activity?.closeKeyboard() {
    this?.currentFocus?.apply {
        inputMethodManager?.apply {
            hideSoftInputFromWindow(windowToken, 0)
        }
    }
}

/**
 * 通过定时器强制隐藏虚拟键盘
 */
fun View?.timerHideKeyboard() {
    this?.let {
        Executors.newSingleThreadScheduledExecutor().schedule({
            it.context.inputMethodManager?.apply {
                if (isActive) {
                    hideSoftInputFromWindow(it.applicationWindowToken, 0)
                }
            }
        }, 10, TimeUnit.MILLISECONDS)
    }
}

/**
 * 切换软键盘的状态
 * 如当前为收起变为弹出,若当前为弹出变为收起
 */
fun EditText?.toggleKeyboard() {
    this?.let {
        it.context.inputMethodManager?.apply {
            toggleSoftInput(
                0,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }
}

/**
 * 强制隐藏输入法键盘
 */
fun EditText?.hideKeyboardForced() {
    this?.let {
        it.context.inputMethodManager?.apply {
            if (isActive) {
                hideSoftInputFromWindow(it.windowToken, 0)
            }
        }
    }
}

/**
 * 输入法是否显示
 */
fun EditText.isKeyboardShowing(): Boolean {
    return context.inputMethodManager?.isActive ?: false
}

/**
 * Fix the leaks of soft input.
 */
fun Window.fixSoftInputLeaks() {
    ContextUtil.context.inputMethodManager?.let {
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
                val obj = leakViewField[it] as? View ?: continue
                if (obj.rootView === decorView.rootView) {
                    leakViewField[it] = null
                }
            } catch (ignore: Throwable) {
            }
        }
    }
}