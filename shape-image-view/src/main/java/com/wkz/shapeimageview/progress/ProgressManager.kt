package com.wkz.shapeimageview.progress

import com.bumptech.glide.load.engine.GlideException
import okhttp3.OkHttpClient
import java.lang.ref.WeakReference
import java.util.*

/**
 * @author Administrator
 */
object ProgressManager {
    private val listeners =
        Collections.synchronizedList(ArrayList<WeakReference<OnProgressListener>>())

    @Volatile
    private var okHttpClient: OkHttpClient? = null

    fun getOkHttpClient(): OkHttpClient {
        if (okHttpClient == null) {
            synchronized(ProgressManager::class.java) {
                if (okHttpClient == null) {
                    okHttpClient = OkHttpClient.Builder()
                        .addNetworkInterceptor { chain ->
                            val request = chain.request()
                            val response = chain.proceed(request)
                            response.newBuilder()
                                .body(
                                    ProgressResponseBody(
                                        request.url().toString(),
                                        response.body(),
                                        LISTENER
                                    )
                                )
                                .build()
                        }
                        .build()
                }
            }
        }
        return okHttpClient!!
    }

    private val LISTENER = object : OnProgressListener {
        override fun onProgress(
            imageUrl: String?,
            bytesRead: Long,
            totalBytes: Long,
            isDone: Boolean,
            exception: GlideException?
        ) {
            if (listeners.size == 0) {
                return
            }
            for (i in listeners.indices) {
                val listener = listeners[i]
                val progressListener = listener.get()
                progressListener?.onProgress(imageUrl, bytesRead, totalBytes, isDone, exception)
                    ?: listeners.removeAt(i)
            }
        }

    }

    fun addProgressListener(progressListener: OnProgressListener?) {
        if (progressListener == null) {
            return
        }
        if (findProgressListener(progressListener) == null) {
            listeners.add(
                WeakReference(
                    progressListener
                )
            )
        }
    }

    fun removeProgressListener(progressListener: OnProgressListener?) {
        if (progressListener == null) {
            return
        }
        val listener =
            findProgressListener(progressListener)
        if (listener != null) {
            listeners.remove(listener)
        }
    }

    private fun findProgressListener(listener: OnProgressListener?): WeakReference<OnProgressListener>? {
        if (listener == null || listeners.size == 0) {
            return null
        }
        for (i in listeners.indices) {
            val progressListener =
                listeners[i]
            if (progressListener.get() === listener) {
                return progressListener
            }
        }
        return null
    }
}