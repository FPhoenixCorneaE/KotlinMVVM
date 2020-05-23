package com.wkz.framework.glide

import android.content.Context
import android.util.Log
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool
import com.bumptech.glide.load.engine.cache.ExternalPreferredCacheDiskCacheFactory
import com.bumptech.glide.load.engine.cache.LruResourceCache
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import java.io.InputStream

/**
 * @document: http://bumptech.github.io/glide/doc/configuration.html#setup
 * @desc: 自定义GlideModule子类，设置内存缓存、Bitmap池、磁盘缓存、默认请求选项、解码格式等等
 */
@GlideModule
class CustomAppGlideModule : AppGlideModule() {

    /**
     * 清单解析的禁用
     * 这里不开启，避免添加相同的modules两次
     * 这样可以改善 Glide 的初始启动时间，并避免尝试解析元数据时的一些潜在问题。
     * @return
     */
    override fun isManifestParsingEnabled() = false


    /**
     *
     * 为App注册一个自定义的String类型的BaseGlideUrlLoader
     * @param context
     * @param glide
     * @param registry
     */
    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        registry.append(
            String::class.java,
            InputStream::class.java,
            CustomBaseGlideUrlLoader.Factory()
        )
    }

    /**
     * 通过GlideBuilder设置默认的结构(Engine,BitmapPool,ArrayPool,MemoryCache等等).
     *
     * @param context
     * @param builder
     */
    override fun applyOptions(context: Context, builder: GlideBuilder) {
        // 磁盘缓存,Glide 使用 DiskLruCacheWrapper 作为默认的磁盘缓存。DiskLruCacheWrapper 是一个使用
        // LRU 算法的固定大小的磁盘缓存。默认磁盘大小为 250 MB，位置是在应用的缓存文件夹中的一个特定目录。
        builder.setDiskCache(
            ExternalPreferredCacheDiskCacheFactory(
                context,
                diskCacheFolderName(context),
                diskCacheSizeBytes()
            )
        )
            // 默认情况下，Glide使用 LruResourceCache，这是 MemoryCache 接口的一个缺省实现，
            // 使用固定大小的内存和 LRU 算法。LruResourceCache 的大小由 Glide 的 MemorySizeCalculator
            // 类来决定，这个类主要关注设备的内存类型，设备 RAM 大小，以及屏幕分辨率
            // 也可以直接覆写内存缓存大小 LruResourceCache(memoryCacheSizeBytes())
            .setMemoryCache(LruResourceCache(memoryCacheSizeBytes()))
            // Bitmap 池,Glide 使用 LruBitmapPool 作为默认的 BitmapPool。LruBitmapPool 是一个内存中的
            // 固定大小的 BitmapPool，使用 LRU 算法清理。默认大小基于设备的分辨率和密度，同时也考虑内存类和
            // isLowRamDevice 的返回值。具体的计算通过 Glide 的 MemorySizeCalculator 来完成，与 Glide 的
            // MemoryCache 的大小检测方法相似。
            .setBitmapPool(LruBitmapPool(bitmapPoolSizeBytes()))
            // 日志级别,通常来说 Log.VERBOSE 将使日志变得更冗杂，Log.ERROR 会让日志更趋向静默
            .setLogLevel(Log.DEBUG)
            // 默认请求选项
            .setDefaultRequestOptions(
                RequestOptions().format(DecodeFormat.PREFER_RGB_565)
                    .disallowHardwareConfig()
            )
    }

    /**
     * set the bitmap pool size, unit is the [Byte].
     */
    private fun bitmapPoolSizeBytes(): Long {
        return 1024 * 1024 * 30 // 30 MB
    }

    /**
     * set the memory cache size, unit is the [Byte].
     */
    private fun memoryCacheSizeBytes(): Long {
        return 1024 * 1024 * 20 // 20 MB
    }

    /**
     * set the disk cache size, unit is the [Byte].
     */
    private fun diskCacheSizeBytes(): Long {
        return 1024 * 1024 * 512 // 512 MB
    }

    /**
     * set the disk cache folder's name.
     */
    private fun diskCacheFolderName(context: Context): String {
        return ContextCompat.getCodeCacheDir(context).path + "/kotlinmvvm"
    }
}
