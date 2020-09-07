package com.fphoenixcorneae.rxretrofit.network

import androidx.annotation.Keep
import java.io.Serializable

/**
 * 封装返回的数据
 * 成员视服务器返回格式而定
 */
@Keep
data class BaseResponse<T>(
    val errorCode: Int,
    val errorMsg: String,
    val data: T?
) : Serializable {
    /**
     * WanAndroid网站返回的 错误码为 0 就代表请求成功
     */
    fun isWanAndroidSuccess(): Boolean = errorCode == 0
}