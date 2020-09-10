package com.fphoenixcorneae.annotation

import androidx.annotation.Keep

/**
 * @desc 时间单位
 */
@Keep
@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE, AnnotationTarget.VALUE_PARAMETER)
@kotlin.annotation.Retention(AnnotationRetention.BINARY)
annotation class TimeUnit {
    companion object {

        /**
         * 毫秒与毫秒的倍数
         */
        const val MILLISECOND: Long = 1
        /**
         * 秒与毫秒的倍数
         */
        const val SECOND: Long = MILLISECOND * 1000
        /**
         * 分与毫秒的倍数
         */
        const val MINUTE: Long = SECOND * 60
        /**
         * 时与毫秒的倍数
         */
        const val HOUR: Long = MINUTE * 60
        /**
         * 天与毫秒的倍数
         */
        const val DAY: Long = HOUR * 24
        /**
         * 月与毫秒的倍数
         */
        const val MONTH: Long = DAY * 30
        /**
         * 年与毫秒的倍数
         */
        const val YEAR: Long = DAY * 365
    }
}
