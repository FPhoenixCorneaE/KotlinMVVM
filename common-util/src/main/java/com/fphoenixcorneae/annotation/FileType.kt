package com.fphoenixcorneae.annotation

import androidx.annotation.Keep

/**
 * @desc：文件类型
 */
@Keep
@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE, AnnotationTarget.VALUE_PARAMETER)
@kotlin.annotation.Retention(AnnotationRetention.BINARY)
annotation class FileType {
    companion object {
        /**
         * 声明各种类型文件的dataType
         */
        const val DATA_TYPE_APK = "application/vnd.android.package-archive"
        const val DATA_TYPE_VIDEO = "video/*"
        const val DATA_TYPE_AUDIO = "audio/*"
        const val DATA_TYPE_HTML = "text/html"
        const val DATA_TYPE_IMAGE = "image/*"
        const val DATA_TYPE_PPT = "application/vnd.ms-powerpoint"
        const val DATA_TYPE_EXCEL = "application/vnd.ms-excel"
        const val DATA_TYPE_WORD = "application/msword"
        const val DATA_TYPE_CHM = "application/x-chm"
        const val DATA_TYPE_TXT = "text/plain"
        const val DATA_TYPE_PDF = "application/pdf"
        /**
         * 未指定明确的文件类型，不能使用精确类型的工具打开，需要用户选择
         */
        const val DATA_TYPE_ALL = "*/*"
    }
}