package com.wkz.unit

/**
 * @author wkz
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
@kotlin.annotation.Retention(AnnotationRetention.BINARY)
annotation class MemoryUnit {
    companion object {

        /**
         * Byte与Byte的倍数
         */
        val BYTE = Math.pow(1024.0, 0.0).toLong()
        /**
         * KB与Byte的倍数
         */
        val KB = Math.pow(1024.0, 1.0).toLong()
        /**
         * MB与Byte的倍数
         */
        val MB = Math.pow(1024.0, 2.0).toLong()
        /**
         * GB与Byte的倍数
         */
        val GB = Math.pow(1024.0, 3.0).toLong()
        /**
         * TB与Byte的倍数
         */
        val TB = Math.pow(1024.0, 4.0).toLong()
        /**
         * PB与Byte的倍数
         */
        val PB = Math.pow(1024.0, 5.0).toLong()
        /**
         * EB与Byte的倍数
         */
        val EB = Math.pow(1024.0, 6.0).toLong()
        /**
         * ZB与Byte的倍数
         */
        val ZB = Math.pow(1024.0, 7.0).toLong()
        /**
         * YB与Byte的倍数
         */
        val YB = Math.pow(1024.0, 8.0).toLong()
        /**
         * BB与Byte的倍数
         */
        val BB = Math.pow(1024.0, 9.0).toLong()
        /**
         * NB与Byte的倍数
         */
        val NB = Math.pow(1024.0, 10.0).toLong()
        /**
         * DB与Byte的倍数
         */
        val DB = Math.pow(1024.0, 11.0).toLong()
        /**
         * CB与Byte的倍数
         */
        val CB = Math.pow(1024.0, 12.0).toLong()
        /**
         * XB与Byte的倍数
         */
        val XB = Math.pow(1024.0, 13.0).toLong()
    }
}
