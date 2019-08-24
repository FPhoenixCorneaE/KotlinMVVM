package com.wkz.kotlinmvvm.test

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.orhanobut.logger.Logger
import com.qingmei2.rximagepicker.core.RxImagePicker
import com.qingmei2.rximagepicker_extension.MimeType
import com.qingmei2.rximagepicker_extension_wechat.WechatConfigrationBuilder
import com.qingmei2.rximagepicker_extension_wechat.ui.WechatImagePickerFragment
import com.tbruyelle.rxpermissions2.RxPermissions
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider
import com.uber.autodispose.autoDisposable
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
import java.util.concurrent.ExecutionException


class TestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

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
                .autoDisposable(AndroidLifecycleScopeProvider.from(this@TestActivity, Lifecycle.Event.ON_DESTROY))
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
                    val originalMode = it.getBooleanExtra(WechatImagePickerFragment.EXTRA_ORIGINAL_IMAGE, false)
                    val mimeType = it.getStringExtra(WechatImagePickerFragment.EXTRA_OPTIONAL_MIME_TYPE, "")
                    Logger.d("select image original:" + originalMode + " , uri path: " + it.uri.path)
                    Logger.d("mime types: $mimeType")

                    it.uri.path?.let { it1 ->
                        object : Thread() {
                            override fun run() {
                                try {
                                    val file = Glide.with(this@TestActivity)
                                        .load(it.uri)
                                        .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                                        .get()
                                    TestModel().uploadImage(
                                        "32834",
                                        ImgBase64Util.imageToBase64("data:image/jpeg;base64," + file.path),
                                        "android/pics"
                                    )
                                } catch (e: InterruptedException) {
                                    Logger.e(e.toString())
                                } catch (e: ExecutionException) {
                                    Logger.e(e.toString())
                                }
                            }
                        }.start()
                    }
                }, {
                    ToastUtil.showShort("Failed:$it")
                })
        }
        mTvGoToHome.setOnClickListener {
            startActivity(Intent(this@TestActivity, OpenEyesHomeActivity::class.java))
        }
    }
}
