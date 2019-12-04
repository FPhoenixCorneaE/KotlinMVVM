package com.wkz.annotation

import kotlin.math.pow

/**
 * @desc 内存单位
 * @author wkz
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE, AnnotationTarget.VALUE_PARAMETER)
@kotlin.annotation.Retention(AnnotationRetention.BINARY)
annotation class MemoryUnit {
    companion object {

        /**
         * Byte与Byte的倍数
         */
        val BYTE = 1024.0.pow(0.0).toLong()
        /**
         * KB与Byte的倍数
         */
        val KB = 1024.0.pow(1.0).toLong()
        /**
         * MB与Byte的倍数
         */
        val MB = 1024.0.pow(2.0).toLong()
        /**
         * GB与Byte的倍数
         */
        val GB = 1024.0.pow(3.0).toLong()
        /**
         * TB与Byte的倍数
         */
        val TB = 1024.0.pow(4.0).toLong()
        /**
         * PB与Byte的倍数
         */
        val PB = 1024.0.pow(5.0).toLong()
        /**
         * EB与Byte的倍数
         */
        val EB = 1024.0.pow(6.0).toLong()
        /**
         * ZB与Byte的倍数
         */
        val ZB = 1024.0.pow(7.0).toLong()
        /**
         * YB与Byte的倍数
         */
        val YB = 1024.0.pow(8.0).toLong()
        /**
         * BB与Byte的倍数
         */
        val BB = 1024.0.pow(9.0).toLong()
        /**
         * NB与Byte的倍数
         */
        val NB = 1024.0.pow(10.0).toLong()
        /**
         * DB与Byte的倍数
         */
        val DB = 1024.0.pow(11.0).toLong()
        /**
         * CB与Byte的倍数
         */
        val CB = 1024.0.pow(12.0).toLong()
        /**
         * XB与Byte的倍数
         */
        val XB = 1024.0.pow(13.0).toLong()
    }
}
