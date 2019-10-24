package com.wkz.wanandroid.mvvm.viewmodel

import androidx.lifecycle.ViewModel
import com.wkz.rxretrofit.network.IBaseUrl
import com.wkz.rxretrofit.network.RetrofitManager
import com.wkz.wanandroid.api.WanAndroidApi
import com.wkz.wanandroid.api.WanAndroidUrlConstant

open class WanAndroidBaseViewModel : ViewModel(), IBaseUrl {

    protected val sWanAndroidService: WanAndroidApi by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        RetrofitManager.getRetrofit(this).create(WanAndroidApi::class.java)
    }

    override fun getBaseUrl(): String = WanAndroidUrlConstant.BASE_URL
}