package com.wkz.shapeimageview.progress

import com.bumptech.glide.load.engine.GlideException

/**
 * @desc：加载进度监听器
 * @author Administrator
 */
interface OnGlideImageViewListener {
    /**
     * 加载进度
     *
     * @param percent   百分比
     * @param isDone    加载失败或者加载完毕
     * @param exception 异常
     */
    fun onProgress(
        percent: Int,
        isDone: Boolean,
        exception: GlideException?
    )
}