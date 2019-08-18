package com.wkz.annotation

/**
 * @desc：形状类型
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE, AnnotationTarget.VALUE_PARAMETER)
@kotlin.annotation.Retention(AnnotationRetention.BINARY)
annotation class ShapeType {
    companion object {
        const val RECTANGLE = 0x01
        const val ROUNDED_RECTANGLE = 0x02
        const val CIRCLE = 0x03
    }
}