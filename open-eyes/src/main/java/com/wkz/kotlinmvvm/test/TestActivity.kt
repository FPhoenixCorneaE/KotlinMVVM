package com.wkz.kotlinmvvm.test

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import com.orhanobut.logger.Logger
import com.qihoo360.replugin.RePlugin
import com.qihoo360.replugin.model.PluginInfo
import com.qihoo360.replugin.utils.FileUtils
import com.qingmei2.rximagepicker.core.RxImagePicker
import com.qingmei2.rximagepicker_extension.MimeType
import com.qingmei2.rximagepicker_extension_wechat.WechatConfigrationBuilder
import com.qingmei2.rximagepicker_extension_wechat.ui.WechatImagePickerFragment
import com.tbruyelle.rxpermissions2.RxPermissions
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider
import com.uber.autodispose.autoDisposable
import com.wkz.extension.showToast
import com.wkz.framework.imagepicker.WeChatImagePicker
import com.wkz.kotlinmvvm.R
import com.wkz.kotlinmvvm.mvvm.viewmodel.activity.OpenEyesHomeActivity
import com.wkz.rxretrofit.scheduler.SchedulerManager
import com.wkz.util.ImgBase64Util
import com.wkz.util.ToastUtil
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_test.*
import okhttp3.MediaType
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.concurrent.TimeUnit


class TestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        initData(savedInstanceState)
    }

    /**
     * 初始化数据
     */
    @SuppressLint("AutoDispose")
    fun initData(savedInstanceState: Bundle?) {
        // {
        //    "userId": "32834",
        //    "page": 0,
        //    "size": 10,
        //    "classType": "e35d6e7951ec4b1e94672e63677e402f",
        //    "rebateStatus": "PENDING"
        //}
        mTvPostJson.setOnClickListener {
            val param = "{\n" +
                    "    \"userId\": \"32834\",\n" +
                    "    \"page\": 0,\n" +
                    "    \"size\": 10,\n" +
                    "    \"classType\": \"e35d6e7951ec4b1e94672e63677e402f\",\n" +
                    "    \"rebateStatus\": \"PENDING\"\n" +
                    "}"
            val body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), param)
            TestModel().queryRebateListByUserId(body)
        }
        mTvPostForm.setOnClickListener {
            TestModel().queryGoodsClass("233")
        }
        mTvPostMultipart.setOnClickListener {
            Observable.just(1)
                .compose(
                    RxPermissions(this@TestActivity).ensure(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                )
                .observeOn(Schedulers.io())
                .filter { t -> t }
                .flatMap {
                    RxImagePicker
                        .create(WeChatImagePicker::class.java)
                        .openGallery(
                            this@TestActivity,
                            WechatConfigrationBuilder(MimeType.ofImage(), false)
                                .capture(true)
                                .maxSelectable(1)
                                .countable(true)
                                .spanCount(4)
                                .countable(false)
                                .build()
                        )
                }
                .compose(SchedulerManager.ioToMain())
                .autoDisposable(
                    AndroidLifecycleScopeProvider.from(
                        this@TestActivity,
                        Lifecycle.Event.ON_DESTROY
                    )
                )
                .subscribe({
                    // Usage
                    // val isGif: Boolean
                    //  get() = if (mimeType == null) false else mimeType == MimeType.GIF.toString()
                    // val isImage: Boolean
                    //  get() = if (mimeType == null) false else mimeType == MimeType.JPEG.toString()
                    //        || mimeType == MimeType.PNG.toString()
                    //        || mimeType == MimeType.GIF.toString()
                    //        || mimeType == MimeType.BMP.toString()
                    //        || mimeType == MimeType.WEBP.toString()
                    val originalMode =
                        it.getBooleanExtra(WechatImagePickerFragment.EXTRA_ORIGINAL_IMAGE, false)
                    val mimeType =
                        it.getStringExtra(WechatImagePickerFragment.EXTRA_OPTIONAL_MIME_TYPE, "")
                    Logger.d("select image original:" + originalMode + " , uri path: " + it.uri.path)
                    Logger.d("mime types: $mimeType")
                    Logger.d("content:/" + it.uri.path)
                    TestModel().uploadImage(
                        "32834",
                        "data:image/jpeg;base64," + ImgBase64Util.imageToBase64("content:/" + it.uri.path),
                        "android/pics"
                    )
                }, {
                    ToastUtil.showShort("Failed:$it")
                })
        }
        mTvGoToHome.setOnClickListener {
            startActivity(Intent(this@TestActivity, OpenEyesHomeActivity::class.java))
        }
        // 卸载插件
        RePlugin.uninstall("PleasedReading")
        mTvGoToPleasedReading.setOnClickListener {
            // TODO 将来把回调串联上
            if (RePlugin.isPluginInstalled("PleasedReading")) {
                // 以“Alias（别名）”来打开
                RePlugin.startActivity(
                    this@TestActivity,
                    RePlugin.createIntent(
                        "com.wkz.pleasedreading",
                        "com.wkz.pleasedreading.splash.PRSplashActivity"
                    )
                )
            } else {
                val progressDialog = ProgressDialog.show(
                    this@TestActivity,
                    "Installing...",
                    "Please wait...",
                    true,
                    true
                )
                Observable.timer(1, TimeUnit.SECONDS)
                    .compose(
                        RxPermissions(this@TestActivity).ensure(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                    )
                    .filter { t -> t }
                    .observeOn(Schedulers.io())
                    .map {
                        simulateInstallExternalPlugin()
                    }
                    .compose(SchedulerManager.ioToMain())
                    .autoDisposable(
                        AndroidLifecycleScopeProvider.from(
                            this@TestActivity,
                            Lifecycle.Event.ON_DESTROY
                        )
                    )
                    .subscribe {
                        progressDialog.dismiss()
                    }
            }
        }
        // 卸载插件
        RePlugin.uninstall("WanAndroid")
        mTvGoToWanAndroid.setOnClickListener {
            // TODO 将来把回调串联上
            if (RePlugin.isPluginInstalled("WanAndroid")) {
                // 以“Alias（别名）”来打开
                RePlugin.startActivity(
                    this@TestActivity,
                    RePlugin.createIntent(
                        "com.wkz.wanandroid",
                        "com.wkz.wanandroid.mvvm.view.activity.WanAndroidHomeActivity"
                    )
                )
            } else {
                val progressDialog = ProgressDialog.show(
                    this@TestActivity,
                    "Installing...",
                    "Please wait...",
                    true,
                    true
                )
                Observable.timer(1, TimeUnit.SECONDS)
                    .compose(
                        RxPermissions(this@TestActivity).ensure(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                    )
                    .filter { t -> t }
                    .observeOn(Schedulers.io())
                    .map {
                        simulateInstallExternalPlugin()
                    }
                    .compose(SchedulerManager.ioToMain())
                    .autoDisposable(
                        AndroidLifecycleScopeProvider.from(
                            this@TestActivity,
                            Lifecycle.Event.ON_DESTROY
                        )
                    )
                    .subscribe {
                        progressDialog.dismiss()
                    }
            }
        }
    }

    /**
     * 模拟安装或升级（覆盖安装）外置插件
     * 注意：为方便演示，外置插件临时放置到Host的assets/external目录下
     */
    private fun simulateInstallExternalPlugin() {
        val apk = "WanAndroid.apk"
        val apkPath = "external" + File.separator + apk

        // 文件是否已经存在？直接删除重来
        val pluginFilePath = filesDir.absolutePath + File.separator + apk
        val pluginFile = File(pluginFilePath)
        if (pluginFile.exists()) {
            FileUtils.deleteQuietly(pluginFile)
        }

        // 开始复制
        copyAssetsFileToAppFiles(apkPath, apk)
        var info: PluginInfo? = null
        if (pluginFile.exists()) {
            info = RePlugin.install(pluginFilePath)
            info?.let {
                RePlugin.preload(info)
            }
        }

        if (info != null) {
            // 以“包名”来打开
            RePlugin.startActivity(
                this@TestActivity,
                RePlugin.createIntent(
                    info.packageName,
                    "com.wkz.wanandroid.mvvm.view.activity.WanAndroidHomeActivity"
                )
            )

            // 以“Alias（别名）”来打开
//            val intent = Intent()
//            intent.component =
//                ComponentName("PleasedReading", "com.wkz.pleasedreading.splash.PRSplashActivity")
//            RePlugin.startActivity(this@TestActivity, intent)
        } else {
            showToast("install external plugins failed")
        }
    }

    /**
     * 从assets目录中复制某文件内容
     * @param  assetFileName assets目录下的Apk源文件路径
     * @param  newFileName 复制到/data/data/package_name/files/目录下文件名
     */
    private fun copyAssetsFileToAppFiles(assetFileName: String, newFileName: String) {
        var `is`: InputStream? = null
        var fos: FileOutputStream? = null

        try {
            `is` = this.assets.open(assetFileName)
            fos = this.openFileOutput(newFileName, Context.MODE_PRIVATE)
            var byteCount = 0
            val buffer = ByteArray(1024)
            while ({ byteCount = `is`.read(buffer, 0, 1024); byteCount }() != -1) {
                fos?.write(buffer, 0, byteCount)
            }
            fos?.flush()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                `is`?.close()
                fos?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
