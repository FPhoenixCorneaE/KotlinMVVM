package com.fphoenixcorneae.util

import com.fphoenixcorneae.annotation.TimeUnit
import com.fphoenixcorneae.ext.loggerE
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * @desc：时间工具类
 */
class TimeUtil {
    init {
        throw UnsupportedOperationException("U can't initialize me...")
    }

    companion object {

        const val DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss"

        /**
         * 获取友好型与当前时间的差
         *
         * time格式为yyyy-MM-dd HH:mm:ss
         *
         * @param time 时间字符串
         * @return 友好型与当前时间的差
         *
         *  * 如果在60秒内，显示刚刚
         *  * 如果在60分钟内，显示XXX分钟前
         *  * 如果超过1小时且24小时内，显示1小时前~23小时前
         *  * 如果超过24小时且48小时内，显示昨天
         *  * 如果超过48小时且30天内，显示2天前~30天前
         *  * 如果超过1个月且1年内，显示1个月前~11个月前
         *  * 其余显示，2016-10-15
         *  * 时间不合法的情况全部日期和时间信息，如星期六 十月 27 14:21:20 CST 2007
         *
         */
        @JvmStatic
        fun getFriendlyTimeSpanByNow(time: String): String {
            return try {
                getFriendlyTimeSpanByNow(time, SimpleDateFormat(DEFAULT_FORMAT, Locale.getDefault()))
            } catch (e: Exception) {
                loggerE(e.toString())
                time
            }

        }

        /**
         * 获取友好型与当前时间的差
         *
         * time格式为format
         *
         * @param time   时间字符串
         * @param format 时间格式
         * @return 友好型与当前时间的差
         *
         *  * 如果在60秒内，显示刚刚
         *  * 如果在60分钟内，显示XXX分钟前
         *  * 如果超过1小时且24小时内，显示1小时前~23小时前
         *  * 如果超过24小时且48小时内，显示昨天
         *  * 如果超过48小时且30天内，显示2天前~30天前
         *  * 如果超过1个月且1年内，显示1个月前~11个月前
         *  * 其余显示，2016-10-15
         *  * 时间不合法的情况全部日期和时间信息，如星期六 十月 27 14:21:20 CST 2007
         *
         */
        @JvmStatic
        fun getFriendlyTimeSpanByNow(time: String, format: DateFormat): String {
            return getFriendlyTimeSpanByNow(string2Millis(time, format))
        }

        /**
         * 获取友好型与当前时间的差
         *
         * @param date Date类型时间
         * @return 友好型与当前时间的差
         *
         *  * 如果在60秒内，显示刚刚
         *  * 如果在60分钟内，显示XXX分钟前
         *  * 如果超过1小时且24小时内，显示1小时前~23小时前
         *  * 如果超过24小时且48小时内，显示昨天
         *  * 如果超过48小时且30天内，显示2天前~30天前
         *  * 如果超过1个月且1年内，显示1个月前~11个月前
         *  * 其余显示，2016-10-15
         *  * 时间不合法的情况全部日期和时间信息，如星期六 十月 27 14:21:20 CST 2007
         *
         */
        @JvmStatic
        fun getFriendlyTimeSpanByNow(date: Date): String {
            return getFriendlyTimeSpanByNow(date.time)
        }

        /**
         * 获取友好型与当前时间的差
         *
         * @param millis 毫秒时间戳
         * @return 友好型与当前时间的差
         *
         *  * 如果在60秒内，显示刚刚
         *  * 如果在60分钟内，显示XXX分钟前
         *  * 如果超过1小时且24小时内，显示1小时前~23小时前
         *  * 如果超过24小时且48小时内，显示昨天
         *  * 如果超过48小时且30天内，显示2天前~30天前
         *  * 如果超过1个月且1年内，显示1个月前~11个月前
         *  * 其余显示，2016-10-15
         *  * 时间不合法的情况全部日期和时间信息，如星期六 十月 27 14:21:20 CST 2007
         *
         */
        @JvmStatic
        fun getFriendlyTimeSpanByNow(millis: Long): String {
            val now = System.currentTimeMillis()
            val span = now - millis
            return when {
                span < 0 -> {
                    String.format(Locale.getDefault(), "%tc", millis)
                }
                span < TimeUnit.MINUTE -> {
                    "刚刚"
                }
                span < TimeUnit.HOUR -> {
                    String.format(Locale.getDefault(), "%d分钟前", span / TimeUnit.MINUTE)
                }
                span < TimeUnit.DAY -> {
                    String.format(Locale.getDefault(), "%d小时前", span / TimeUnit.HOUR)
                }
                span < 2 * TimeUnit.DAY -> {
                    String.format("昨天%tR", millis)
                }
                span < TimeUnit.MONTH -> {
                    String.format(Locale.getDefault(), "%d天前", span / TimeUnit.DAY)
                }
                span < TimeUnit.YEAR -> {
                    String.format(Locale.getDefault(), "%d个月前", span / TimeUnit.MONTH)
                }
                else -> {
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(millis)
                }
            }
        }

        /**
         * 将时间字符串转为时间戳
         *
         * time格式为format
         *
         * @param time   时间字符串
         * @param format 时间格式
         * @return 毫秒时间戳
         */
        @JvmStatic
        fun string2Millis(time: String, format: DateFormat): Long {
            try {
                return format.parse(time)!!.time
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            return -1
        }

        /**
         * 将时间戳转为时间字符串
         *
         * 格式为format
         *
         * @param millis 毫秒时间戳
         * @param format 时间格式
         * @return 时间字符串
         */
        @JvmStatic
        fun millis2String(millis: Long, format: DateFormat): String {
            return format.format(Date(millis))
        }

        /**
         * 获取当前时间字符串
         *
         * 格式为yyyy-MM-dd HH:mm:ss
         *
         * @return 时间字符串
         */
        @JvmStatic
        val nowString: String
            get() = millis2String(System.currentTimeMillis(), SimpleDateFormat(DEFAULT_FORMAT, Locale.getDefault()))
    }
}
