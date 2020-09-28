package com.fphoenixcorneae.openeyes.mvvm.model

import com.fphoenixcorneae.openeyes.api.OpenEyesApi
import com.fphoenixcorneae.openeyes.api.OpenEyesUrlConstant
import com.fphoenixcorneae.rxretrofit.network.IBaseUrl
import com.fphoenixcorneae.rxretrofit.network.RetrofitManager
import com.fphoenixcorneae.util.AppUtil
import com.fphoenixcorneae.util.DeviceIdUtil

/**
 * @desc：开眼基类 Model
 */
open class OpenEyesBaseModel : IBaseUrl {

    protected val sOpenEyesService: OpenEyesApi by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        RetrofitManager.addQueryParameter("udid", DeviceIdUtil.uniqueID)
            .addQueryParameter("deviceModel", AppUtil.getMobileModel())
            .getRetrofit(this)
            .create(OpenEyesApi::class.java)
    }

    override fun getBaseUrl(): String {
        return OpenEyesUrlConstant.BASE_URL
    }
}