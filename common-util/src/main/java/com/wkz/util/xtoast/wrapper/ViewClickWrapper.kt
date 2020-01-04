package com.wkz.util.xtoast.wrapper

import android.view.View
import com.wkz.util.xtoast.XToast
import com.wkz.util.xtoast.listener.OnClickListener

/**
 * [View.OnClickListener] 包装类
 */
class ViewClickWrapper(
    private val mToast: XToast,
    view: View,
    private val mListener: OnClickListener
) : View.OnClickListener {
    override fun onClick(v: View) {
        mListener.onClick(mToast, v)
    }

    init {
        view.setOnClickListener(this)
    }
}