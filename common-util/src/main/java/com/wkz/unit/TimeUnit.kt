package com.wkz.unit

/**
 * @author wkz
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
@kotlin.annotation.Retention(AnnotationRetention.BINARY)
annotation class TimeUnit {
    companion object {

        /**
         * 毫秒与毫秒的倍数
         */
        val MILLISECOND = 1L
        /**
         * 秒与毫秒的倍数
         */
        val SECOND = 1000L
        /**
         * 分与毫秒的倍数
         */
        val MINUTE = 60 * 1000L
        /**
         * 时与毫秒的倍数
         */
        val HOUR = 60 * 60 * 1000L
        /**
         * 天与毫秒的倍数
         */
        val DAY = 24 * 60 * 60 * 1000L
        /**
         * 月与毫秒的倍数
         */
        val MONTH = 30 * 24 * 60 * 60 * 1000L
        /**
         * 年与毫秒的倍数
         */
        val YEAR = 365 * 24 * 60 * 60 * 1000L
    }
}
