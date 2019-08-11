package com.wkz.extension

import android.content.Context
import com.wkz.util.SizeUtil
import com.wkz.util.ToastUtil

/**
 * @desc: 扩展
 */
fun showToast(content: CharSequence) {
    ToastUtil.showShort(content)
}

fun dp2px(dipValue: Float): Int {
    return SizeUtil.dp2px(dipValue)
}

fun px2dp(pxValue: Float): Int {
    return SizeUtil.px2dp(pxValue)
}

/**
 * 持续时间格式化
 */
fun durationFormat(duration: Long?): String {
    val minute = duration!! / 60
    val second = duration % 60
    return if (minute <= 9) {
        if (second <= 9) {
            "0$minute' 0$second''"
        } else {
            "0$minute' $second''"
        }
    } else {
        if (second <= 9) {
            "$minute' 0$second''"
        } else {
            "$minute' $second''"
        }
    }
}

/**
 * 数据流量格式化
 */
fun dataFormat(total: Long): String {
    val result: String
    val speedReal: Int = (total / (1024)).toInt()
    result = if (speedReal < 512) {
        speedReal.toString() + " KB"
    } else {
        val mSpeed = speedReal / 1024.0
        (Math.round(mSpeed * 100) / 100.0).toString() + " MB"
    }
    return result
}




