package com.wkz.kotlinmvvm.mvp.model

import com.wkz.framework.base.IBaseModel
import com.wkz.kotlinmvvm.api.OpenEyesApi
import com.wkz.kotlinmvvm.api.UrlConstant
import com.wkz.rxretrofit.network.IBaseUrl
import com.wkz.rxretrofit.network.RetrofitManager

/**
 * @desc：开眼基类 Model
 */
open class OpenEyesBaseModel : IBaseUrl, IBaseModel {

    val sOpenEyesService: OpenEyesApi by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        RetrofitManager.getRetrofit(this).create(OpenEyesApi::class.java)
    }

    override fun getBaseUrl(): String {
        return UrlConstant.BASE_URL
    }
}