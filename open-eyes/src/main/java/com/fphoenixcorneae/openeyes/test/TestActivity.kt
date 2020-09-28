package com.fphoenixcorneae.openeyes.test

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import com.fphoenixcorneae.ext.loggerD
import com.fphoenixcorneae.ext.toast
import com.fphoenixcorneae.framework.imagepicker.WeChatImagePicker
import com.fphoenixcorneae.openeyes.R
import com.fphoenixcorneae.openeyes.mvvm.viewmodel.activity.OpenEyesHomeActivity
import com.fphoenixcorneae.rxretrofit.scheduler.SchedulerManager
import com.fphoenixcorneae.util.ImgBase64Util
import com.fphoenixcorneae.util.SharedPreferencesUtil
import com.qingmei2.rximagepicker.core.RxImagePicker
import com.qingmei2.rximagepicker_extension.MimeType
import com.qingmei2.rximagepicker_extension_wechat.WechatConfigrationBuilder
import com.qingmei2.rximagepicker_extension_wechat.ui.WechatImagePickerFragment
import com.tbruyelle.rxpermissions2.RxPermissions
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider
import com.uber.autodispose.autoDisposable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_test.*
import okhttp3.MediaType
import okhttp3.RequestBody


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
            //            TestModel().queryGoodsClass("233")
            TestModel().getBindQrCode("", "2f0879bde06e4dd9a62ac791817d4100")
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
                    loggerD("select image original:" + originalMode + " , uri path: " + it.uri.path)
                    loggerD("mime types: $mimeType")
                    loggerD("content:/" + it.uri.path)
                    TestModel().uploadImage(
                        "32834",
                        "data:image/jpeg;base64," + ImgBase64Util.imageToBase64("content:/" + it.uri.path),
                        "android/pics"
                    )
                }, {
                    toast("Failed:$it")
                })
        }
        mTvGoToHome.setOnClickListener {
            startActivity(Intent(this@TestActivity, OpenEyesHomeActivity::class.java))
        }
        mTvGoToPleasedReading.setOnClickListener {
            mTvGoToPleasedReading.text = "token" + SharedPreferencesUtil.getString("token")
        }
        mTvGoToWanAndroid.setOnClickListener {

        }
    }
}
