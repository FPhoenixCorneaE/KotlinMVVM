package com.wkz.annotation

/**
 * @desc 时间单位
 * @author wkz
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE, AnnotationTarget.VALUE_PARAMETER)
@kotlin.annotation.Retention(AnnotationRetention.BINARY)
annotation class TimeUnit {
    companion object {

        /**
         * 毫秒与毫秒的倍数
         */
        const val MILLISECOND: Long = 1L
        /**
         * 秒与毫秒的倍数
         */
        const val SECOND: Long = 1000L
        /**
         * 分与毫秒的倍数
         */
        const val MINUTE: Long = 60 * 1000L
        /**
         * 时与毫秒的倍数
         */
        const val HOUR: Long = 60 * 60 * 1000L
        /**
         * 天与毫秒的倍数
         */
        const val DAY: Long = 24 * 60 * 60 * 1000L
        /**
         * 月与毫秒的倍数
         */
        const val MONTH: Long = 30 * 24 * 60 * 60 * 1000L
        /**
         * 年与毫秒的倍数
         */
        const val YEAR: Long = 365 * 24 * 60 * 60 * 1000L
    }
}
