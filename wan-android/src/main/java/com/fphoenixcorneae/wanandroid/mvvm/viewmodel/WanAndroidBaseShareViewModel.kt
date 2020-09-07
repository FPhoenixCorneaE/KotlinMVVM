package com.fphoenixcorneae.wanandroid.mvvm.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.fphoenixcorneae.rxretrofit.network.IBaseUrl
import com.fphoenixcorneae.rxretrofit.network.RetrofitManager
import com.fphoenixcorneae.util.AppUtil
import com.fphoenixcorneae.util.DeviceIdUtil
import com.fphoenixcorneae.util.LanguageUtil
import com.fphoenixcorneae.wanandroid.api.WanAndroidApi
import com.fphoenixcorneae.wanandroid.api.WanAndroidUrlConstant

/**
 * @desc: 页面之间数据共享ViewModel
 */
open class WanAndroidBaseShareViewModel(application: Application) : AndroidViewModel(application),
    IBaseUrl {

    protected val sWanAndroidService: WanAndroidApi by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        RetrofitManager.addQueryParameter("deviceId", DeviceIdUtil.uniqueID)
            .addHeader("deviceModel", AppUtil.getMobileModel())
            .addHeader("versionCode", AppUtil.versionCode.toString())
            .addHeader("versionName", AppUtil.versionName)
            .addHeader("language", LanguageUtil.currentLocale.toString())
            .addHeader("sign", AppUtil.getSign())
            .getRetrofit(this)
            .create(WanAndroidApi::class.java)
    }

    override fun getBaseUrl(): String = WanAndroidUrlConstant.BASE_URL
}