package com.wkz.shapeimageview.progress

import com.bumptech.glide.load.engine.GlideException

/**
 * @author Administrator
 */
interface OnProgressListener {
    fun onProgress(
        imageUrl: String?,
        bytesRead: Long,
        totalBytes: Long,
        isDone: Boolean,
        exception: GlideException?
    )
}