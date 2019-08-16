package com.wkz.kotlinmvvm.mvvm.model

import com.wkz.kotlinmvvm.api.OpenEyesApi
import com.wkz.kotlinmvvm.api.OpenEyesUrlConstant
import com.wkz.rxretrofit.network.IBaseUrl
import com.wkz.rxretrofit.network.RetrofitManager

/**
 * @desc：开眼基类 Model
 */
open class OpenEyesBaseModel : IBaseUrl {

    val sOpenEyesService: OpenEyesApi by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        RetrofitManager.getRetrofit(this).create(OpenEyesApi::class.java)
    }

    override fun getBaseUrl(): String {
        return OpenEyesUrlConstant.BASE_URL
    }
}