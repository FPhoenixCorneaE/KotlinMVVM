package com.wkz.framework.glide

import android.graphics.Bitmap
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.widget.ImageView

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes

import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.wkz.util.ResourceUtil

/**
 * Glide加载图片工具类
 * 不要在非主线程里面使用Glide加载图片，如果真的使用了，请把context参数换成getApplicationContext.
 * 创建Glide的主要目的有两个，一个是实现平滑的图片列表滚动效果，另一个是支持远程图片的获取、大小调整和展示。
 * Glide特点:
 * 1.使用简单
 * 2.可配置度高，自适应程度高
 * 3.支持常见图片格式 jpeg png gif webp
 * 4.支持多种数据源  网络、本地、资源、Assets 等
 * 5.高效缓存策略    支持Memory和Disk图片缓存 默认Bitmap格式采用RGB_565内存使用至少减少一半
 * 6.生命周期集成    根据Activity/Fragment生命周期自动管理请求
 * 7.高效处理Bitmap  使用Bitmap Pool使Bitmap复用，主动调用recycle回收需要回收的Bitmap，减小系统回收压力
 */
object GlideUtil {

    fun setupImagePlaceColorRes(imageView: ImageView, picUrl: Any?, @ColorRes placeResId: Int) {
        setupImage(imageView, picUrl, ColorDrawable(ResourceUtil.getColor(placeResId)))
    }

    fun setupImagePlaceDrawableRes(
        imageView: ImageView,
        picUrl: Any?, @DrawableRes placeResId: Int
    ) {
        setupImage(imageView, picUrl, ResourceUtil.getDrawable(placeResId))
    }

    /**
     * @JvmOverloads 重载方法
     * 设置图片
     */
    @JvmOverloads
    fun setupImage(imageView: ImageView, picUrl: Any?, placeholder: Drawable? = null) {
        setupImageRequestOptions(
            imageView,
            picUrl,
            RequestOptions().centerCrop()
                .placeholder(placeholder)
                .error(placeholder)
        )
    }

    fun setupImageRequestOptions(imageView: ImageView, picUrl: Any?, options: RequestOptions) {
        setupImage(imageView, picUrl, options, null)
    }

    fun setupRoundedImagePlaceColorRes(
        imageView: ImageView,
        picUrl: Any?,
        cornerRadius: Int,
        @ColorRes placeResId: Int
    ) {
        setupRoundedImage(
            imageView,
            picUrl,
            cornerRadius,
            ColorDrawable(ResourceUtil.getColor(placeResId))
        )
    }

    fun setupRoundedImagePlaceDrawableRes(
        imageView: ImageView,
        picUrl: Any?,
        cornerRadius: Int,
        @DrawableRes placeResId: Int
    ) {
        setupRoundedImage(imageView, picUrl, cornerRadius, ResourceUtil.getDrawable(placeResId))
    }

    /**
     * @JvmOverloads 重载方法
     * 设置圆角图片
     * @param cornerRadius 圆角
     */
    @JvmOverloads
    fun setupRoundedImage(
        imageView: ImageView,
        picUrl: Any?,
        cornerRadius: Int,
        placeholder: Drawable? = null
    ) {
        setupRoundedImageRequestOptions(
            imageView,
            picUrl,
            RequestOptions().transform(CenterCrop(), RoundedCorners(cornerRadius))
                .placeholder(placeholder)
                .error(placeholder)
        )
    }

    fun setupRoundedImageRequestOptions(
        imageView: ImageView,
        picUrl: Any?,
        options: RequestOptions
    ) {
        setupImage(imageView, picUrl, options, null)
    }

    fun setupCircleImagePlaceColorRes(
        imageView: ImageView,
        picUrl: Any?, @ColorRes placeResId: Int
    ) {
        setupCircleImage(imageView, picUrl, ColorDrawable(ResourceUtil.getColor(placeResId)))
    }

    fun setupCircleImagePlaceDrawableRes(
        imageView: ImageView,
        picUrl: Any?, @DrawableRes placeResId: Int
    ) {
        setupCircleImage(imageView, picUrl, ResourceUtil.getDrawable(placeResId))
    }

    /**
     * @JvmOverloads 重载方法
     * 设置圆形图片
     */
    @JvmOverloads
    fun setupCircleImage(imageView: ImageView, picUrl: Any?, placeholder: Drawable? = null) {
        setupCircleImageRequestOptions(
            imageView,
            picUrl,
            RequestOptions()
                .transform(CenterCrop(), CircleCrop())
                .placeholder(placeholder)
                .error(placeholder)
        )
    }

    fun setupCircleImageRequestOptions(
        imageView: ImageView,
        picUrl: Any?,
        options: RequestOptions
    ) {
        setupImage(imageView, picUrl, options, null)
    }

    /**
     * 设置图片
     *
     *
     * 问题：有时候使用placeholder(int drawble)图片会显示不出来。
     * 解决：可以使用 .dontAnimate()使得图片在有占位图的同时下载并且实时显示出图片
     * 原理： .dontAnimate()只是一个巧妙的角度，归根结底是由于Glide它会为每种大小的ImageView缓存 一次。
     * 尽管一张图片已经缓存了一次，但是假如你要在另外一个地方再次以不同尺寸显示，需要重新下载，调整成新尺寸的大小，然后将这个尺寸的也缓存起来。
     * 所以不同于Picasso，ImageView只要变化尺寸，图片就需要重新下载，所以会有图片不显示的错觉。
     * 所以可以使用.diskCacheStrategy(DiskCacheStrategy.ALL)来缓存所有不同形状的图片，解决问题。
     *
     * @param imageView 展示图片ImageView或ImageView子类
     * @param picUrl    图片路径,可以为一个文件路径、uri或者url,也可以为本地图片，Uri类型图片，资源图片，byte[]类型图片，甚至可以是自定义类型图片
     */
    fun setupImage(
        imageView: ImageView,
        picUrl: Any?,
        requestOptions: RequestOptions,
        requestListener: RequestListener<Drawable>?
    ) {
        getRequestBuilder(imageView, picUrl, requestListener).apply(requestOptions).into(imageView)
    }

    /**
     * thumbnail 缩略图支持:这样会先加载缩略图，然后在加载全图，参数为缩略比例
     * DiskCacheStrategy 策略解说：all:缓存源资源和转换后的资源 none:不作任何磁盘缓存 source:缓存源资源 result：缓存转换后的资源
     *
     * @param imageView 展示图片ImageView或ImageView子类
     * @param picUrl    图片路径
     * @return RequestBuilder
     */
    private fun getRequestBuilder(
        imageView: ImageView,
        picUrl: Any?,
        requestListener: RequestListener<Drawable>?
    ): RequestBuilder<Drawable> {
        return GlideApp.with(imageView)
            .load(picUrl)
            .skipMemoryCache(false)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .encodeFormat(Bitmap.CompressFormat.WEBP)
            .thumbnail(0.5f)
            .transition(DrawableTransitionOptions.withCrossFade())
            .listener(requestListener)
    }
}
