## **RxRetrofit**

### **基于ViewModel,LiveData,Retrofit2和RxJava2封装的网络请求框架**

#### **线程池调度器封装：SchedulerManager**
- **ComputationMainScheduler**
```kotlin
    /**
     * 用于计算任务，如事件循环或和回调处理，不要用于IO操作(IO操作请使用Schedulers.io())；
     * 默认线程数等于处理器的数量
     */
    fun <T> computationToMain(): ComputationMainScheduler<T> {
        return ComputationMainScheduler()
    }
```
- **IoMainScheduler**
```kotlin
    /**
     * 用于IO密集型任务，如异步阻塞IO操作，这个调度器的线程池会根据需要增长；
     * 对于普通的计算任务，请使用Schedulers.computation()；
     * Schedulers.io()默认是一个CachedThreadScheduler，很像一个有线程缓存的新线程调度器
     */
    fun <T> ioToMain(): IoMainScheduler<T> {
        return IoMainScheduler()
    }
```
- **NewThreadMainScheduler**
```kotlin
    /**
     * 为每个任务创建一个新线程
     */
    fun <T> newThreadToMain(): NewThreadMainScheduler<T> {
        return NewThreadMainScheduler()
    }
```
- **SingleMainScheduler**
```kotlin
    /**
     * 该调度器的线程池只能同时执行一个线程
     */
    fun <T> singleToMain(): SingleMainScheduler<T> {
        return SingleMainScheduler()
    }
```
- **TrampolineMainScheduler**
```kotlin
    /**
     * 当其它排队的任务完成后，在当前线程排队开始执行
     */
    fun <T> trampolineToMain(): TrampolineMainScheduler<T> {
        return TrampolineMainScheduler()
    }
```

#### **异常处理封装**
- **网络连接超时**
    **SocketTimeoutException**, **ConnectException**, **UnknownHostException**, **HttpException** 均视为网络错误
- **数据解析异常（或者第三方数据结构更改）等其他异常**
    **JsonParseException**, **JSONException**, **ParseException** 均视为解析错误
- **服务器内部错误**
    **ApiException**
- **参数错误**
    **IllegalArgumentException**
- **证书验证错误**
    **SSLException**
- **未知错误**

#### **Retrofit封装**
- **添加公共参数**
```kotlin
    /**
     * 添加公共参数(直接接在url后面)
     */
    fun addQueryParameter(name: String, value: String?): RetrofitManager {
        mQueryParameterArray[name] = value
        return this
    }
```
- **添加公共请求头**
```kotlin
    /**
     * 添加公共请求头
     */
    fun addHeader(name: String, value: String): RetrofitManager {
        mHeaderArray[name] = value
        return this
    }
```
- **获取retrofit的实例,LiveDataCallAdapterFactory支持LiveData,RxJava2CallAdapterFactory支持Observable**
```kotlin
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
            .addConverterFactory(GsonConverterFactory.create(GSON))
            .build()
    }
```
```kotlin
    /**
     * 登录
     */
    @FormUrlEncoded
    @POST("user/login")
    fun login(@Field("username") userName: String, @Field("password") password: String): LiveData<BaseResponse<WanAndroidUserInfoBean>>
```
```kotlin
    /**
     * 首页精选
     */
    @GET("v2/feed?")
    fun getFirstHomeData(@Query("num") num: Int): Observable<OpenEyesHomeBean>
```
- **获取OkHttpClient,添加SSL证书**
```kotlin
    /**
     * 获取OkHttpClient
     */
    private fun getOkHttpClient(): OkHttpClient {
        // SSL证书
        val sslParams = SslSocketUtils.getSslSocketFactory()
        // 添加一个log拦截器,打印所有的log
        val httpLoggingInterceptor = HttpLoggingInterceptor { message ->
            loggerI(message)
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
            .hostnameVerifier(SslSocketUtils.mUnSafeHostnameVerifier)
            // 错误重连
            .retryOnConnectionFailure(true)
            // 添加缓存文件与大小
            .cache(cache)
            .connectTimeout(60L, TimeUnit.SECONDS)
            .readTimeout(60L, TimeUnit.SECONDS)
            .writeTimeout(60L, TimeUnit.SECONDS)
            .build()
    }
```
- **添加公共参数拦截器**
```kotlin
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
```
- **添加请求头拦截器**
```kotlin
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
```
- **添加缓存拦截器**
```kotlin
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
```
- **自由配置BaseUrl,Model需实现IBaseUrl接口**
```kotlin
open class WanAndroidBaseViewModel : ViewModel(), IBaseUrl {

    protected val sWanAndroidService: WanAndroidApi by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        RetrofitManager.addQueryParameter("deviceId", DeviceIdUtil.uniqueID)
            .addHeader("deviceModel", AppUtil.getMobileModel())
            .addHeader("versionCode", AppUtil.versionCode.toString())
            .addHeader("versionName", AppUtil.versionName)
            .addHeader("sign", AppUtil.getSign())
            .getRetrofit(this)
            .create(WanAndroidApi::class.java)
    }

    override fun getBaseUrl(): String = WanAndroidUrlConstant.BASE_URL
}
```