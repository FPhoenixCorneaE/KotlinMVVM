class Deps {
    static gradle_version = '4.2.0'
    static kotlin_version = '1.6.10'
    static navigation_version = '2.3.5'

    static classpath = [
            gradle            : "com.android.tools.build:gradle:$gradle_version",
            kotlin            : "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version",
            navigationSafeArgs: "androidx.navigation:navigation-safe-args-gradle-plugin:$navigation_version",
    ]

    /** 组件 */
    static FPhoenixCorneaE = [
            CommonUtil            : "com.github.FPhoenixCorneaE:CommonUtil:1.0.4",
            FlowLayout            : "com.github.FPhoenixCorneaE:FlowLayout:1.0.7",
            ShapeImageView        : "com.github.FPhoenixCorneaE:ShapeImageView:1.0.5",
            PrettyDialog          : "com.github.FPhoenixCorneaE:PrettyDialog:1.0.1",
            AvatarWaveView        : 'com.github.FPhoenixCorneaE:AvatarWaveView:1.0.1',
            EasyBehavior          : 'com.github.FPhoenixCorneaE:EasyBehavior:1.0.1',
            BottomNavigationView  : 'com.github.FPhoenixCorneaE:BottomNavigationView:1.0.3',
            ExpandCollapseTextView: 'com.github.FPhoenixCorneaE:ExpandCollapseTextView:1.0.1',
            CustomKeyboard        : 'com.github.FPhoenixCorneaE:CustomKeyboard:1.0.1',
            CustomSpan            : 'com.github.FPhoenixCorneaE:CustomSpan:1.0.0',
            CommonTitlebar        : 'com.github.FPhoenixCorneaE:CommonTitlebar:1.0.2',
            AnimatedNavigation    : 'com.github.FPhoenixCorneaE:AnimatedNavigation:1.0.0',
    ]

    /** Android */
    static android = [
            compileSdkVersion: 31,
            buildToolsVersion: "31.0.0",
            minSdkVersion    : 21,
            targetSdkVersion : 31,
            versionCode      : 1000,
            versionName      : "1.0.0.0"
    ]

    /** Supports */
    static support = [
            appcompat           : "androidx.appcompat:appcompat:1.2.0",
            constraintLayout    : "androidx.constraintlayout:constraintlayout:2.0.0",
            material            : "com.google.android.material:material:1.5.0",
            recyclerView        : "androidx.recyclerview:recyclerview:1.1.0",
            cardView            : "androidx.cardview:cardview:1.0.0",
            lifecycleViewmodel  : "androidx.lifecycle:lifecycle-viewmodel-ktx:2.2.0-alpha02",
            lifecycleExtensions : "androidx.lifecycle:lifecycle-extensions:2.2.0-alpha02",
            lifecycleRuntime    : 'androidx.lifecycle:lifecycle-runtime-ktx:2.2.0',
            lifecycleCommonJava8: 'androidx.lifecycle:lifecycle-common-java8:2.2.0',
            flexbox             : "com.google.android:flexbox:2.0.1",
            viewpager2          : "androidx.viewpager2:viewpager2:1.0.0",
            palette             : "androidx.palette:palette-ktx:1.0.0",
    ]

    /* Android 换肤框架 */
    static androidSkinSupport = [
            skinSupport                : 'skin.support:skin-support:4.0.5',
            skinSupportAppcompat       : 'skin.support:skin-support-appcompat:4.0.5',
            skinSupportDesign          : 'skin.support:skin-support-design:4.0.5',
            skinSupportCardView        : 'skin.support:skin-support-cardview:4.0.5',
            skinSupportConstraintLayout: 'skin.support:skin-support-constraint-layout:4.0.5',
    ]

    /** navigation 扩展插件 */
    static navigation = [
            common  : "androidx.navigation:navigation-common-ktx:$navigation_version",
            fragment: "androidx.navigation:navigation-fragment-ktx:$navigation_version",
            runtime : "androidx.navigation:navigation-runtime-ktx:$navigation_version",
            ui      : "androidx.navigation:navigation-ui-ktx:$navigation_version",
    ]

    /** Kotlin */
    static kotlin = [
            core            : "androidx.core:core-ktx:1.3.1",
            activity        : "androidx.activity:activity-ktx:1.2.0",
            fragment        : "androidx.fragment:fragment-ktx:1.3.0",
            kotlinStdlibJdk7: "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlin_version",
            coroutines      : 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.2',
    ]

    /** Retrofit2 */
    static retrofit2 = [
            retrofit                 : "com.squareup.retrofit2:retrofit:2.9.0",
            retrofitConverterGson    : "com.squareup.retrofit2:converter-gson:2.9.0",
            retrofitAdapterRxjava2   : "com.squareup.retrofit2:adapter-rxjava2:2.9.0",
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
            glide           : "com.github.bumptech.glide:glide:4.11.0",
            glideAnnotations: "com.github.bumptech.glide:annotations:4.11.0",
            glideOkhttp     : "com.github.bumptech.glide:okhttp3-integration:4.11.0",
    ]

    /** 下拉刷新、上拉加载、二级刷新、淘宝二楼、RefreshLayout、OverScroll，Android智能下拉刷新框架，
     * 支持越界回弹、越界拖动，具有极强的扩展性，集成了几十种炫酷的Header和 Footer
     * 注意：分包之后不会有默认的Header和Footer需要手动添加！还是原来的三种方法！*/
    static smartRefresh = [
            // 核心必须依赖
            layoutKernel  : 'com.scwang.smart:refresh-layout-kernel:2.0.1',
            // 经典刷新头
            headerClassics: 'com.scwang.smart:refresh-header-classics:2.0.1',
            // 雷达刷新头
            headerRadar   : 'com.scwang.smart:refresh-header-radar:2.0.1',
            // 虚拟刷新头
            headerFalsify : 'com.scwang.smart:refresh-header-falsify:2.0.1',
            // 谷歌刷新头
            headerMaterial: 'com.scwang.smart:refresh-header-material:2.0.1',
            // 二级刷新头
            headerTwoLevel: 'com.scwang.smart:refresh-header-two-level:2.0.1',
            // 球脉冲加载
            footerBall    : 'com.scwang.smart:refresh-footer-ball:2.0.1',
            // 经典加载
            footerClassics: 'com.scwang.smart:refresh-footer-classics:2.0.1',
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

    /** 腾讯Bugly异常上报*/
    static bugly = [
            crashReport      : 'com.tencent.bugly:crashreport:3.4.4',
            nativeCrashReport: 'com.tencent.bugly:nativecrashreport:3.9.2'
    ]

    /* MaterialDialogs */
    static materialDialogs = [
            core        : 'com.afollestad.material-dialogs:core:3.3.0',
            input       : 'com.afollestad.material-dialogs:input:3.3.0',
            files       : 'com.afollestad.material-dialogs:files:3.3.0',
            color       : 'com.afollestad.material-dialogs:color:3.3.0',
            datetime    : 'com.afollestad.material-dialogs:datetime:3.3.0',
            bottomsheets: 'com.afollestad.material-dialogs:bottomsheets:3.3.0',
            lifecycle   : 'com.afollestad.material-dialogs:lifecycle:3.3.0',
    ]

    /* QRCode 扫描二维码、扫描条形码、相册获取图片后识别、生成带 Logo 二维码、支持微博微信 QQ 二维码扫描样式 */
    static qrCode = [
            zxing: 'cn.bingoogolapple:bga-qrcode-zxing:1.3.7',
            zbar : 'cn.bingoogolapple:bga-qrcode-zbar:1.3.7',
    ]

    /** 直接在layout文件中去创建drawable */
    static folivora = "cn.cricin:folivora:0.1.0"
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
    /** Cookies自动持久化 */
    static persistentCookieJar = 'com.github.franmontiel:PersistentCookieJar:v1.0.1'
    /** Kotlin Coroutines Image Loader */
    static coil = 'io.coil-kt:coil:0.13.0'
    /** 性能优化工具 */
    static leakCanary = "com.squareup.leakcanary:leakcanary-android:2.5"


    /** compiler */
    static dataBindingCompiler = "com.android.databinding:compiler:3.1.4"
    static glideCompiler = "com.github.bumptech.glide:compiler:4.11.0"
    static dagger2Compiler = [
            daggerCompiler        : 'com.google.dagger:dagger-compiler:2.16',
            daggerAndroidProcessor: 'com.google.dagger:dagger-android-processor:2.16',
    ]


    /** kapt/annotationProcessor */
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
    static buglyLibs = bugly.values()
    static materialDialogsLibs = materialDialogs.values()
    static androidSkinSupportLibs = androidSkinSupport.values()
    static FPhoenixCorneaELibs = FPhoenixCorneaE.values()
    static qrCodeLibs = qrCode.values()
}