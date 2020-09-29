package com.fphoenixcorneae.openeyes.mvvm.model

import android.os.Build
import com.fphoenixcorneae.openeyes.api.OpenEyesApi
import com.fphoenixcorneae.openeyes.api.OpenEyesUrlConstant
import com.fphoenixcorneae.rxretrofit.network.IBaseUrl
import com.fphoenixcorneae.rxretrofit.network.RetrofitManager
import com.fphoenixcorneae.util.AppUtil
import com.fphoenixcorneae.util.DeviceIdUtil
import com.fphoenixcorneae.util.RomUtil
import com.fphoenixcorneae.util.ScreenUtil
import java.util.*

/**
 * @desc 开眼基类 Model
 */
open class OpenEyesBaseModel : IBaseUrl {

    protected val sOpenEyesService: OpenEyesApi by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        RetrofitManager
            .addHeader("model", "Android")
            .addHeader("If-Modified-Since", "${Date()}")
            .addHeader("User-Agent", System.getProperty("http.agent") ?: "unknown")
            .addQueryParameter("udid", DeviceIdUtil.uniqueID)
            .addQueryParameter("deviceModel", AppUtil.getMobileModel())
            .addQueryParameter("vc", AppUtil.versionCode.toString())
            .addQueryParameter("vn", AppUtil.versionName)
            .addQueryParameter("size", "${ScreenUtil.screenWidth}X${ScreenUtil.screenHeight}")
            .addQueryParameter("first_channel", RomUtil.romInfo?.name)
            .addQueryParameter("last_channel", RomUtil.romInfo?.name)
            .addQueryParameter("system_version_code", "${Build.VERSION.SDK_INT}")
            .getRetrofit(this)
            .create(OpenEyesApi::class.java)
    }

    override fun getBaseUrl(): String {
        return OpenEyesUrlConstant.BASE_URL
    }
}