package com.wkz.rxretrofit.network

import androidx.collection.ArrayMap
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import com.orhanobut.logger.Logger
import com.wkz.rxretrofit.network.factory.LiveDataCallAdapterFactory
import com.wkz.rxretrofit.network.ssl.SslSocketUtil
import com.wkz.util.ContextUtil
import com.wkz.util.NetworkUtil
import com.wkz.util.gson.GsonUtil
import okhttp3.*
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

    /**
     * 持久化CookieJar
     */
    private val mPersistentCookieJar: PersistentCookieJar by lazy {
        PersistentCookieJar(SetCookieCache(), SharedPrefsCookiePersistor(ContextUtil.context))
    }
    private var mQueryParameterArray = ArrayMap<String, String?>()
    private var mHeaderArray = ArrayMap<String, String>()

    /**
     * 添加公共参数拦截器
     */
    private fun addQueryParameterInterceptor(): Interceptor {
        return Interceptor { chain ->
            val originalRequest = chain.request()
            val builder = originalRequest.url().newBuilder()
            // Provide your custom QueryParameter here
            mQueryParameterArray.apply {
                forEach { (name, value) ->
                    builder.addQueryParameter(name, value)
                }
                // 添加完清空
                clear()
            }
            val modifiedUrl = builder.build()
            val request = originalRequest.newBuilder().url(modifiedUrl).build()
            chain.proceed(request)
        }
    }

    /**
     * 添加请求头拦截器
     */
    private fun addHeaderInterceptor(): Interceptor {
        return Interceptor { chain ->
            val originalRequest = chain.request()
            val requestBuilder = originalRequest.newBuilder()
            // Provide your custom header here
            mHeaderArray.apply {
                forEach { (name, value) ->
                    requestBuilder.addHeader(name, value)
                }
                // 添加完清空
                clear()
            }
            requestBuilder.method(originalRequest.method(), originalRequest.body())
            val request = requestBuilder.build()
            chain.proceed(request)
        }
    }

    /**
     * 添加缓存拦截器
     */
    private fun addCacheInterceptor(): Interceptor {
        return Interceptor { chain ->
            var request = chain.request()
            val response: Response =
                when {
                    NetworkUtil.isConnected -> {
                        val maxAge = 0
                        // 有网络时 设置缓存超时时间0个小时 ,意思就是不读取缓存数据,只对get有用,post没有缓冲
                        chain.proceed(request).newBuilder()
                            // 清除头信息，因为服务器如果不支持，会返回一些干扰信息，不清除下面无法生效
                            .removeHeader("Pragma")
                            .removeHeader("Cache-Control")
                            .header("Cache-Control", "public, max-age=$maxAge")
                            .build()
                    }
                    else -> {
                        // 无网络时，设置缓存超时为4周  只对get有用,post没有缓冲
                        // 没有缓存时会报异常：Exception:retrofit2.adapter.rxjava2.HttpException:
                        // HTTP 504 Unsatisfiable Request (only-if-cached)
                        request = request.newBuilder()
                            .cacheControl(
                                CacheControl.Builder()
                                    .onlyIfCached()
                                    .maxStale(
                                        60 * 60 * 24 * 28,
                                        TimeUnit.SECONDS
                                    ).build()
                            )
                            .build()
                        chain.proceed(request)
                            .newBuilder()
                            .build()
                    }
                }
            response
        }
    }

    /**
     * 获取OkHttpClient
     */
    private fun getOkHttpClient(): OkHttpClient {
        // SSL证书
        val sslParams = SslSocketUtil.getSslSocketFactory()
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
            .addNetworkInterceptor(httpLoggingInterceptor)
            // 信任所有证书,不安全有风险
            .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
            .hostnameVerifier(SslSocketUtil.mUnSafeHostnameVerifier)
            // 错误重连
            .retryOnConnectionFailure(true)
            // 添加缓存文件与大小
            .cache(cache)
            // 添加Cookies自动持久化
            .cookieJar(mPersistentCookieJar)
            // 连接超时时间
            .connectTimeout(10L, TimeUnit.SECONDS)
            // 读超时时间
            .readTimeout(10L, TimeUnit.SECONDS)
            // 写超时时间
            .writeTimeout(10L, TimeUnit.SECONDS)
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
            .addConverterFactory(GsonConverterFactory.create(GsonUtil.gson))
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

    /**
     * 清空Cookies
     */
    fun clearCookieJar() {
        mPersistentCookieJar.clear()
    }
}
