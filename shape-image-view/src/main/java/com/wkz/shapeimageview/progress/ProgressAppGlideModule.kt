package com.wkz.shapeimageview.progress

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.LibraryGlideModule
import java.io.InputStream

/**
 * 自定义GlideModule子类，设置内存缓存、Bitmap池、磁盘缓存、默认请求选项、解码格式等等
 *
 * @author Administrator
 */
@GlideModule
class ProgressAppGlideModule : LibraryGlideModule() {

    /**
     *
     * 为App注册一个自定义的String类型的BaseGlideUrlLoader
     * @param context
     * @param glide
     * @param registry
     */
    override fun registerComponents(
        context: Context,
        glide: Glide,
        registry: Registry
    ) {
        registry.replace(
            GlideUrl::class.java,
            InputStream::class.java,
            OkHttpUrlLoader.Factory(ProgressManager.getOkHttpClient())
        )
    }
}