package com.fphoenixcorneae.annotation

import androidx.annotation.Keep

/**
 * @desc 内存单位
 */
@Keep
@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE, AnnotationTarget.VALUE_PARAMETER)
@kotlin.annotation.Retention(AnnotationRetention.BINARY)
annotation class MemoryUnit {
    companion object {

        /**
         * Byte与Byte的倍数
         */
        const val BYTE: Long = 1
        /**
         * KB与Byte的倍数
         */
        const val KB: Long = BYTE * 1024
        /**
         * MB与Byte的倍数
         */
        const val MB: Long = KB * 1024
        /**
         * GB与Byte的倍数
         */
        const val GB: Long = MB * 1024
        /**
         * TB与Byte的倍数
         */
        const val TB: Long = GB * 1024
        /**
         * PB与Byte的倍数
         */
        const val PB: Long = TB * 1024
        /**
         * EB与Byte的倍数
         */
        const val EB: Long = PB * 1024
        /**
         * ZB与Byte的倍数
         */
        const val ZB: Long = EB * 1024
        /**
         * YB与Byte的倍数
         */
        const val YB: Long = ZB * 1024
        /**
         * BB与Byte的倍数
         */
        const val BB: Long = YB * 1024
        /**
         * NB与Byte的倍数
         */
        const val NB: Long = BB * 1024
        /**
         * DB与Byte的倍数
         */
        const val DB: Long = NB * 1024
        /**
         * CB与Byte的倍数
         */
        const val CB: Long = DB * 1024
        /**
         * XB与Byte的倍数
         */
        const val XB: Long = CB * 1024
    }
}
