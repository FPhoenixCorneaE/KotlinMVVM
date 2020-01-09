class Config {
    static gradle_version = '3.5.3'
    static kotlin_version = '1.3.61'

    static classpath = [
            gradle: "com.android.tools.build:gradle:$Config.gradle_version",
            kotlin: "org.jetbrains.kotlin:kotlin-gradle-plugin:$Config.kotlin_version",
    ]

    static android = [
            compileSdkVersion: 29,
            buildToolsVersion: "29.0.0",
            minSdkVersion    : 21,
            targetSdkVersion : 29,
            versionCode      : 1000,
            versionName      : "1.0.0.0"
    ]

    static support = [
            appcompat          : "androidx.appcompat:appcompat:1.0.2",
            constraintLayout   : "androidx.constraintlayout:constraintlayout:1.1.3",
            material           : "com.google.android.material:material:1.0.0",
            recyclerView       : "androidx.recyclerview:recyclerview:1.1.0",
            cardView           : "androidx.cardview:cardview:1.0.0",
            lifecycleViewmodel : "androidx.lifecycle:lifecycle-viewmodel-ktx:2.2.0-alpha02",
            lifecycleExtensions: "androidx.lifecycle:lifecycle-extensions:2.2.0-alpha02",
            flexbox            : "com.google.android:flexbox:1.1.0",
            viewpager2         : "androidx.viewpager2:viewpager2:1.0.0",
    ]

    static navigation = [
            common  : 'androidx.navigation:navigation-common-ktx:2.0.0',
            fragment: 'androidx.navigation:navigation-fragment-ktx:2.0.0',
            runtime : 'androidx.navigation:navigation-runtime-ktx:2.0.0',
            ui      : 'androidx.navigation:navigation-ui-ktx:2.0.0',
    ]

    static kotlin = [
            core            : "androidx.core:core-ktx:1.0.2",
            fragment        : "androidx.fragment:fragment-ktx:1.0.0",
            kotlinStdlibJdk7: "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$Config.kotlin_version",
    ]

    static retrofit2 = [
            retrofit                 : "com.squareup.retrofit2:retrofit:2.3.0",
            retrofitConverterGson    : "com.squareup.retrofit2:converter-gson:2.3.0",
            retrofitAdapterRxjava2   : "com.squareup.retrofit2:adapter-rxjava2:2.3.0",
            okhttp3LoggingInterceptor: 'com.squareup.okhttp3:logging-interceptor:3.4.1',
    ]

    static rxJava2 = [
            rxJava   : "io.reactivex.rxjava2:rxjava:2.2.0",
            rxAndroid: "io.reactivex.rxjava2:rxandroid:2.1.0",
            rxKotlin : "io.reactivex.rxjava2:rxkotlin:2.2.0",
    ]

    static rxImagePicker = [
            rximagepicker               : 'com.github.qingmei2:rximagepicker:2.5.1',
            rximagepicker_support       : 'com.github.qingmei2:rximagepicker_support:2.5.1',
            rximagepicker_support_zhihu : 'com.github.qingmei2:rximagepicker_support_zhihu:2.5.1',
            rximagepicker_support_wechat: 'com.github.qingmei2:rximagepicker_support_wechat:2.5.1',
    ]

    static dagger2 = [
            dagger              : 'com.google.dagger:dagger:2.16',
            daggerAndroid       : 'com.google.dagger:dagger-android:2.16',
            daggerandroidSupport: 'com.google.dagger:dagger-android-support:2.16',
    ]

    static autoDispose = [
            autoDisposeAndroid                 : 'com.uber.autodispose:autodispose-android:1.1.0',
            autoDisposeKtx                     : 'com.uber.autodispose:autodispose-ktx:1.1.0',
            autoDisposeAndroidKtx              : 'com.uber.autodispose:autodispose-android-ktx:1.1.0',
            autoDisposeAndroidArchcomponentsKtx:
                    'com.uber.autodispose:autodispose-android-archcomponents-ktx:1.1.0',
    ]

    static glide = [
            glide      : "com.github.bumptech.glide:glide:4.9.0",
            glideOkhttp: "com.github.bumptech.glide:okhttp3-integration:4.9.0",
    ]

    static smartRefresh = [
            smartRefreshLayout: "com.scwang.smartrefresh:SmartRefreshLayout:1.0.3",
            smartRefreshHeader: "com.scwang.smartrefresh:SmartRefreshHeader:1.0.3",
    ]

    static multiType = [
            multiType      : 'me.drakeet.multitype:multitype:3.4.4',
            multiTypeKotlin: 'me.drakeet.multitype:multitype-kotlin:3.4.4',
    ]

    static folivora = "cn.cricin:folivora:0.0.9"

    /** compiler */
    static dataBindingCompiler = "com.android.databinding:compiler:3.1.4"
    static glideCompiler = "com.github.bumptech.glide:compiler:4.9.0"
    static dagger2Compiler = [
            daggerCompiler        : 'com.google.dagger:dagger-compiler:2.16',
            daggerAndroidProcessor: 'com.google.dagger:dagger-android-processor:2.16',
    ]


    static logger = "com.orhanobut:logger:2.2.0"
    static rxPermissions = 'com.github.tbruyelle:rxpermissions:0.10.2'
    static videoPlayer = 'com.shuyu:GSYVideoPlayer:2.1.1'
    static gson = 'com.google.code.gson:gson:2.8.6'

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

    /** kapt */
    static kaptDataBinding = [dataBindingCompiler]
    static kaptGlide = [glideCompiler]
    static kaptDagger2 = dagger2Compiler.values()
}