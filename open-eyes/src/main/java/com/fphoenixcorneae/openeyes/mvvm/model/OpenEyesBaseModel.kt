package com.fphoenixcorneae.openeyes.mvvm.model

import com.fphoenixcorneae.openeyes.api.OpenEyesApi
import com.fphoenixcorneae.openeyes.api.OpenEyesUrlConstant
import com.fphoenixcorneae.rxretrofit.network.IBaseUrl
import com.fphoenixcorneae.rxretrofit.network.RetrofitManager

/**
 * @desc：开眼基类 Model
 */
open class OpenEyesBaseModel : IBaseUrl {

    protected val sOpenEyesService: OpenEyesApi by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        RetrofitManager.getRetrofit(this).create(OpenEyesApi::class.java)
    }

    override fun getBaseUrl(): String {
        return OpenEyesUrlConstant.BASE_URL
    }
}