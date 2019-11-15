package com.wkz.rxretrofit.network

import androidx.collection.ArrayMap
import com.orhanobut.logger.Logger
import com.wkz.rxretrofit.network.factory.LiveDataCallAdapterFactory
import com.wkz.util.ContextUtil
import com.wkz.util.NetworkUtil
import com.wkz.util.SharedPreferencesUtil
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * @desc: Retrofit管理者
 */
object RetrofitManager {

    private var token: String by SharedPreferencesUtil("token", "")
    private var mQueryParameterArray = ArrayMap<String, String?>()
    private var mHeaderArray = ArrayMap<String, String>()

    /**
     * 设置公共参数
     */
    private fun addQueryParameterInterceptor(): Interceptor {
        return Interceptor { chain ->
            val originalRequest = chain.request()
            val builder = originalRequest.url().newBuilder()
            // Provide your custom QueryParameter here
            mQueryParameterArray.forEach { (name, value) ->
                builder.addQueryParameter(name, value)
            }
            val modifiedUrl = builder.build()
            val request = originalRequest.newBuilder().url(modifiedUrl).build()
            chain.proceed(request)
        }
    }

    /**
     * 设置请求头
     */
    private fun addHeaderInterceptor(): Interceptor {
        return Interceptor { chain ->
            val originalRequest = chain.request()
            val requestBuilder = originalRequest.newBuilder()
            // Provide your custom header here
            mHeaderArray.forEach { (name, value) ->
                requestBuilder.addHeader(name, value)
            }
            requestBuilder.method(originalRequest.method(), originalRequest.body())
            val request = requestBuilder.build()
            chain.proceed(request)
        }
    }

    /**
     * 设置缓存
     */
    private fun addCacheInterceptor(): Interceptor {
        return Interceptor { chain ->
            var request = chain.request()
            if (!NetworkUtil.isConnected) {
                request = request.newBuilder()
                    .cacheControl(CacheControl.FORCE_CACHE)
                    .build()
            }
            val response = chain.proceed(request)
            if (NetworkUtil.isConnected) {
                val maxAge = 0
                // 有网络时 设置缓存超时时间0个小时 ,意思就是不读取缓存数据,只对get有用,post没有缓冲
                response.newBuilder()
                    .header("Cache-Control", "public, max-age=$maxAge")
                    // 清除头信息，因为服务器如果不支持，会返回一些干扰信息，不清除下面无法生效
                    .removeHeader("Retrofit")
                    .build()
            } else {
                // 无网络时，设置超时为4周  只对get有用,post没有缓冲
                val maxStale = 60 * 60 * 24 * 28
                response.newBuilder()
                    .header("Cache-Control", "public, only-if-cached, max-stale=$maxStale")
                    .removeHeader("nyn")
                    .build()
            }
            response
        }
    }

    private fun getOkHttpClient(): OkHttpClient {
        // 添加一个log拦截器,打印所有的log
        val httpLoggingInterceptor = HttpLoggingInterceptor { message ->
            Logger.i(message)
        }
        // 可以设置请求过滤的水平,body,basic,headers
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        // 设置请求的缓存的大小跟位置
        val cacheFile = File(ContextUtil.context.cacheDir, "networkCache")
        val cache = Cache(cacheFile, 1024 * 1024 * 50) //50Mb 缓存的大小
        return OkHttpClient.Builder()
            // 共同请求参数添加
            .addInterceptor(addQueryParameterInterceptor())
            // 请求头过滤
            .addInterceptor(addHeaderInterceptor())
            // 设置缓存
            .addInterceptor(addCacheInterceptor())
            // 日志打印
            .addInterceptor(httpLoggingInterceptor)
            // 添加缓存文件与大小
            .cache(cache)
            .connectTimeout(60L, TimeUnit.SECONDS)
            .readTimeout(60L, TimeUnit.SECONDS)
            .writeTimeout(60L, TimeUnit.SECONDS)
            .build()
    }

    /**
     * 获取retrofit的实例
     */
    fun getRetrofit(iBaseUrl: IBaseUrl): Retrofit {
        return Retrofit.Builder()
            // 自由配置BaseUrl,Model需实现IBaseUrl接口
            .baseUrl(iBaseUrl.getBaseUrl())
            .client(getOkHttpClient())
            // 添加LiveDataCallAdapterFactory支持LiveData
            .addCallAdapterFactory(LiveDataCallAdapterFactory.create())
            // Retrofit接口的返回值分为两部分，一部分是前面的Call或者Observable，另一部分是泛型
            // Call类型是默认支持的(内部由DefaultCallAdapterFactory支持)，而如果要支持Observable，我们就需要自己添加RxJava2CallAdapterFactory
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            // Retrofit只支持返回值的第二部分是ResponseBody.class和Void.class类型的或者请求参数为ResponseBody.class
            // 添加GsonConverterFactory可以对服务器的数据进行解析
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * 添加公共参数(直接接在url后面)
     */
    fun addQueryParameter(name: String, value: String?): RetrofitManager {
        mQueryParameterArray[name] = value
        return this
    }

    /**
     * 添加公共请求头
     */
    fun addHeader(name: String, value: String): RetrofitManager {
        mHeaderArray[name] = value
        return this
    }
}
