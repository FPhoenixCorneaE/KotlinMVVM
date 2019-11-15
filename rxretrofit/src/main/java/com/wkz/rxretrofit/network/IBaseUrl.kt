package com.wkz.rxretrofit.network

/**
 * @desc: 自由配置BaseUrl,Model需实现IBaseUrl接口
 */
interface IBaseUrl {

    fun getBaseUrl(): String
}