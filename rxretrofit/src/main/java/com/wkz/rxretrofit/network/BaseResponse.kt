package com.wkz.rxretrofit.network

import androidx.annotation.Keep

/**
 * 封装返回的数据
 * 成员视服务器返回格式而定
 */
@Keep
data class BaseResponse<T>(
    val errorCode: Int,
    val errorMsg: String,
    val data: T?
)