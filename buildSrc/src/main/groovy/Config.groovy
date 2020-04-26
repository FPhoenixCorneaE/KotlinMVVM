class Config {
    static gradle_version = '3.6.3'
    static kotlin_version = '1.3.72'

    static classpath = [
            gradle: "com.android.tools.build:gradle:$Config.gradle_version",
            kotlin: "org.jetbrains.kotlin:kotlin-gradle-plugin:$Config.kotlin_version",
    ]

    /** Android */
    static android = [
            compileSdkVersion: 29,
            buildToolsVersion: "29.0.0",
            minSdkVersion    : 21,
            targetSdkVersion : 29,
            versionCode      : 1000,
            versionName      : "1.0.0.0"
    ]

    /** Supports */
    static support = [
            appcompat           : "androidx.appcompat:appcompat:1.1.0",
            constraintLayout    : "androidx.constraintlayout:constraintlayout:1.1.3",
            material            : "com.google.android.material:material:1.0.0",
            recyclerView        : "androidx.recyclerview:recyclerview:1.1.0",
            cardView            : "androidx.cardview:cardview:1.0.0",
            lifecycleViewmodel  : "androidx.lifecycle:lifecycle-viewmodel-ktx:2.2.0-alpha02",
            lifecycleExtensions : "androidx.lifecycle:lifecycle-extensions:2.2.0-alpha02",
            lifecycleRuntime    : 'androidx.lifecycle:lifecycle-runtime-ktx:2.2.0',
            lifecycleCommonJava8: 'androidx.lifecycle:lifecycle-common-java8:2.2.0',
            flexbox             : "com.google.android:flexbox:1.1.0",
            viewpager2          : "androidx.viewpager2:viewpager2:1.0.0",
            palette             : "androidx.palette:palette-ktx:1.0.0",
    ]

    /** navigation 扩展插件 */
    static navigation = [
            common  : 'androidx.navigation:navigation-common-ktx:2.0.0',
            fragment: 'androidx.navigation:navigation-fragment-ktx:2.0.0',
            runtime : 'androidx.navigation:navigation-runtime-ktx:2.0.0',
            ui      : 'androidx.navigation:navigation-ui-ktx:2.0.0',
    ]

    /** Kotlin */
    static kotlin = [
            core            : "androidx.core:core-ktx:1.0.2",
            activity        : "androidx.activity:activity-ktx:1.2.0-alpha02",
            fragment        : "androidx.fragment:fragment-ktx:1.3.0-alpha02",
            kotlinStdlibJdk7: "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$Config.kotlin_version",
    ]

    /** Retrofit2 */
    static retrofit2 = [
            retrofit                 : "com.squareup.retrofit2:retrofit:2.3.0",
            retrofitConverterGson    : "com.squareup.retrofit2:converter-gson:2.3.0",
            retrofitAdapterRxjava2   : "com.squareup.retrofit2:adapter-rxjava2:2.3.0",
            okhttp3LoggingInterceptor: 'com.squareup.okhttp3:logging-interceptor:3.4.1',
    ]

    /** RxJava2 */
    static rxJava2 = [
            rxJava   : "io.reactivex.rxjava2:rxjava:2.2.0",
            rxAndroid: "io.reactivex.rxjava2:rxandroid:2.1.0",
            rxKotlin : "io.reactivex.rxjava2:rxkotlin:2.2.0",
    ]

    /** 灵活的图片选择器,知乎、微信主题，可自定义主题 */
    static rxImagePicker = [
            rximagepicker               : 'com.github.qingmei2:rximagepicker:2.5.1',
            rximagepicker_support       : 'com.github.qingmei2:rximagepicker_support:2.5.1',
            rximagepicker_support_zhihu : 'com.github.qingmei2:rximagepicker_support_zhihu:2.5.1',
            rximagepicker_support_wechat: 'com.github.qingmei2:rximagepicker_support_wechat:2.5.1',
    ]

    /** Dagger2 */
    static dagger2 = [
            dagger              : 'com.google.dagger:dagger:2.16',
            daggerAndroid       : 'com.google.dagger:dagger-android:2.16',
            daggerandroidSupport: 'com.google.dagger:dagger-android-support:2.16',
    ]

    /** AutoDispose,解决RxJava内存泄漏 */
    static autoDispose = [
            autoDisposeAndroid                 : 'com.uber.autodispose:autodispose-android:1.1.0',
            autoDisposeKtx                     : 'com.uber.autodispose:autodispose-ktx:1.1.0',
            autoDisposeAndroidKtx              : 'com.uber.autodispose:autodispose-android-ktx:1.1.0',
            autoDisposeAndroidArchcomponentsKtx:
                    'com.uber.autodispose:autodispose-android-archcomponents-ktx:1.1.0',
    ]

    /** Glide */
    static glide = [
            glide      : "com.github.bumptech.glide:glide:4.11.0",
            glideOkhttp: "com.github.bumptech.glide:okhttp3-integration:4.11.0",
    ]

    /** 下拉刷新、上拉加载、二级刷新、淘宝二楼、RefreshLayout、OverScroll，Android智能下拉刷新框架，
     * 支持越界回弹、越界拖动，具有极强的扩展性，集成了几十种炫酷的Header和 Footer */
    static smartRefresh = [
            smartRefreshLayout: "com.scwang.smartrefresh:SmartRefreshLayout:1.1.0",
            smartRefreshHeader: "com.scwang.smartrefresh:SmartRefreshHeader:1.1.0",
    ]

    /** MultiTypeAdapter for RecyclerView */
    static multiType = [
            multiType      : 'me.drakeet.multitype:multitype:3.4.4',
            multiTypeKotlin: 'me.drakeet.multitype:multitype-kotlin:3.4.4',
    ]

    /** 侧滑 */
    static smartSwipe = [
            smartSwipe : 'com.billy.android:smart-swipe:1.1.2',
            smartSwipeX: 'com.billy.android:smart-swipe-x:1.1.0',
    ]

    /** AgentWeb */
    static agentWeb = [
            agentweb     : 'com.just.agentweb:agentweb:4.1.2',
            filechooser  : 'com.just.agentweb:filechooser:4.1.2',
            coolIndicator: 'com.github.Justson:CoolIndicator:v1.0.0',
            downloader   : 'com.download.library:Downloader:4.1.2',
    ]


    /** 直接在layout文件中去创建drawable */
    static folivora = "cn.cricin:folivora:0.0.9"
    /** logger */
    static logger = "com.orhanobut:logger:2.2.0"
    /** 权限管理 */
    static rxPermissions = 'com.github.tbruyelle:rxpermissions:0.10.2'
    /** 视屏播放器 */
    static videoPlayer = 'com.shuyu:GSYVideoPlayer:2.1.1'
    /** Gson */
    static gson = 'com.google.code.gson:gson:2.8.6'
    /** 模糊透明View */
    static realtimeBlurView = 'com.github.mmin18:realtimeblurview:1.2.1'
    /** Lottie */
    static lottie = 'com.airbnb.android:lottie:3.4.0'
    /** WorkManager */
    static workManager = 'androidx.work:work-runtime-ktx:2.3.3'
    /** Ken Burns effect */
    static kenBurnsView = 'com.flaviofaria:kenburnsview:1.0.7'


    /** compiler */
    static dataBindingCompiler = "com.android.databinding:compiler:3.1.4"
    static glideCompiler = "com.github.bumptech.glide:compiler:4.11.0"
    static dagger2Compiler = [
            daggerCompiler        : 'com.google.dagger:dagger-compiler:2.16',
            daggerAndroidProcessor: 'com.google.dagger:dagger-android-processor:2.16',
    ]


    /** kapt */
    static kaptDataBinding = [dataBindingCompiler]
    static kaptGlide = [glideCompiler]
    static kaptDagger2 = dagger2Compiler.values()


    static supportLibs = support.values()
    static navigationLibs = navigation.values()
    static kotlinLibs = kotlin.values()
    static retrofit2Libs = retrofit2.values()
    static rxJava2Libs = rxJava2.values()
    static rxImagePickerLibs = rxImagePicker.values()
    static dagger2Libs = dagger2.values()
    static autoDisposeLibs = autoDispose.values()
    static glideLibs = glide.values()
    static smartRefreshLibs = smartRefresh.values()
    static multiTypeLibs = multiType.values()
    static smartSwipeLibs = smartSwipe.values()
    static agentWebLibs = agentWeb.values()
}