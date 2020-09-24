package com.fphoenixcorneae.ext

import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

/**
 * 持续时间格式化
 */
fun durationFormat(duration: Long): String {
    val minute = duration / 60
    val second = duration % 60
    return when {
        minute <= 9 -> when {
            second <= 9 -> "0$minute:0$second"
            else -> "0$minute:$second"
        }
        else -> when {
            second <= 9 -> "$minute:0$second"
            else -> "$minute:$second"
        }
    }
}

/**
 * 数据流量格式化
 */
fun dataFormat(total: Long): String {
    val speedReal: Int = (total / (1024)).toInt()
    return when {
        speedReal < 512 -> "$speedReal KB"
        else -> {
            val mSpeed = speedReal / 1024.0
            ((mSpeed * 100).roundToInt() / 100.0).toString() + " MB"
        }
    }
}

/**
 * 时间格式化
 * @param pattern     时间格式
 * @param msecsValue  毫秒值
 */
fun timeFormat(pattern: String, msecsValue: Long): String {
    val simpleDateFormat = SimpleDateFormat(pattern, Locale.getDefault())
    // 设置时区，否则会有时差
    simpleDateFormat.timeZone = TimeZone.getTimeZone("UT+08:00")
    return simpleDateFormat.format(msecsValue)
}