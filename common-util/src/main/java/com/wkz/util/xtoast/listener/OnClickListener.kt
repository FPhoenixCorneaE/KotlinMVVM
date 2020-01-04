package com.wkz.util.xtoast.listener

import android.view.View
import com.wkz.util.xtoast.XToast

/**
 * View 的点击事件封装
 */
interface OnClickListener {
    fun onClick(toast: XToast?, view: View)
}