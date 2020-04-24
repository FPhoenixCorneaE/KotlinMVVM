package com.wkz.wanandroid.mvvm.viewmodel

import androidx.lifecycle.ViewModel
import com.wkz.rxretrofit.network.IBaseUrl
import com.wkz.rxretrofit.network.RetrofitManager
import com.wkz.util.AppUtil
import com.wkz.util.DeviceIdUtil
import com.wkz.util.LanguageUtil
import com.wkz.wanandroid.api.WanAndroidApi
import com.wkz.wanandroid.api.WanAndroidUrlConstant

open class WanAndroidBaseViewModel : ViewModel(), IBaseUrl {

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