package com.wkz.shapeimageview

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.TransitionOptions
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.wkz.shapeimageview.progress.OnGlideImageViewListener
import com.wkz.shapeimageview.progress.OnProgressListener
import com.wkz.shapeimageview.progress.ProgressManager
import java.lang.ref.WeakReference

/**
 * 图片加载器
 *
 * @author Administrator
 */
class GlideImageLoader(imageView: ImageView) {
    private val mImageView: WeakReference<ImageView>?
    private var mImageUrlObj: Any? = null
    private var mTotalBytes: Long = 0
    private var mLastBytesRead: Long = 0
    private var mLastStatus = false
    private val mMainThreadHandler: Handler
    private var internalProgressListener: OnProgressListener? = null
    private var onGlideImageViewListener: OnGlideImageViewListener? = null
    private var onProgressListener: OnProgressListener? = null
    val imageView: ImageView?
        get() = mImageView?.get()

    val context: Context?
        get() = if (imageView != null) imageView!!.context else null

    val imageUrl: String?
        get() = if (mImageUrlObj == null || mImageUrlObj !is String) null else mImageUrlObj as String?

    fun load(obj: Any?, vararg placeholder: Int) {
        load(obj, requestOptions(*placeholder))
    }

    fun load(obj: Any?, options: RequestOptions?) {
        if (context == null) {
            return
        }
        requestBuilder(obj, options).into(imageView!!)
    }

    fun load(
        obj: Any?,
        transitionOptions: TransitionOptions<*, in Drawable?>?,
        vararg placeholder: Int
    ) {
        if (context == null || transitionOptions == null) {
            return
        }
        requestBuilder(obj, requestOptions(*placeholder)).transition(transitionOptions)
            .into(imageView!!)
    }

    fun requestBuilder(
        obj: Any?,
        options: RequestOptions?
    ): RequestBuilder<Drawable?> {
        mImageUrlObj = obj
        return Glide.with(context!!)
            .load(obj)
            .apply(options!!)
            .listener(object : RequestListener<Drawable?> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any,
                    target: Target<Drawable?>,
                    isFirstResource: Boolean
                ): Boolean {
                    mainThreadCallback(mLastBytesRead, mTotalBytes, true, e)
                    ProgressManager.removeProgressListener(internalProgressListener)
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any,
                    target: Target<Drawable?>,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    mainThreadCallback(mLastBytesRead, mTotalBytes, true, null)
                    ProgressManager.removeProgressListener(internalProgressListener)
                    return false
                }
            })
    }

    fun requestOptions(@DrawableRes vararg placeholder: Int): RequestOptions {
        val requestOptions = RequestOptions()
        return when {
            placeholder.isEmpty() -> {
                requestOptions
            }
            placeholder.size == 1 -> {
                requestOptions.placeholder(placeholder[0])
                    .error(placeholder[0])
            }
            else -> {
                requestOptions.placeholder(placeholder[0])
                    .error(placeholder[1])
            }
        }
    }

    fun requestOptions(vararg placeholder: Drawable?): RequestOptions {
        val requestOptions = RequestOptions()
        return when {
            placeholder.isEmpty() -> {
                requestOptions
            }
            placeholder.size == 1 -> {
                requestOptions.placeholder(placeholder[0])
                    .error(placeholder[0])
            }
            else -> {
                requestOptions.placeholder(placeholder[0])
                    .error(placeholder[1])
            }
        }
    }

    fun circleRequestOptions(@DrawableRes vararg placeholder: Int): RequestOptions {
        return requestOptions(*placeholder)
            .transform(CircleCrop())
    }

    fun circleRequestOptions(vararg placeholder: Drawable?): RequestOptions {
        return requestOptions(*placeholder)
            .transform(CircleCrop())
    }

    private fun addProgressListener() {
        if (imageUrl == null) {
            return
        }
        val url = imageUrl
        if (!url!!.startsWith(HTTP)) {
            return
        }
        internalProgressListener = object : OnProgressListener {
            override fun onProgress(
                imageUrl: String?,
                bytesRead: Long,
                totalBytes: Long,
                isDone: Boolean,
                exception: GlideException?
            ) {
                if (totalBytes == 0L) {
                    return
                }
                if (url != imageUrl) {
                    return
                }
                if (mLastBytesRead == bytesRead && mLastStatus == isDone) {
                    return
                }
                mLastBytesRead = bytesRead
                mTotalBytes = totalBytes
                mLastStatus = isDone
                mainThreadCallback(bytesRead, totalBytes, isDone, exception)
                if (isDone) {
                    ProgressManager.removeProgressListener(this)
                }
            }
        }
        ProgressManager.addProgressListener(internalProgressListener)
    }

    private fun mainThreadCallback(
        bytesRead: Long,
        totalBytes: Long,
        isDone: Boolean,
        exception: GlideException?
    ) {
        mMainThreadHandler.post {
            val percent = (bytesRead * 1.0f / totalBytes * 100.0f).toInt()
            if (onProgressListener != null) {
                onProgressListener!!.onProgress(
                    mImageUrlObj as String?,
                    bytesRead,
                    totalBytes,
                    isDone,
                    exception
                )
            }
            if (onGlideImageViewListener != null) {
                onGlideImageViewListener!!.onProgress(percent, isDone, exception)
            }
        }
    }

    fun setOnGlideImageViewListener(onGlideImageViewListener: OnGlideImageViewListener?) {
        this.onGlideImageViewListener = onGlideImageViewListener
        addProgressListener()
    }

    fun setOnProgressListener(onProgressListener: OnProgressListener?) {
        this.onProgressListener = onProgressListener
        addProgressListener()
    }

    companion object {
        private const val HTTP = "http"
    }

    init {
        mImageView = WeakReference(imageView)
        mMainThreadHandler = Handler(Looper.getMainLooper())
    }
}