package com.fphoenixcorneae.rxretrofit.network.exception

object ErrorStatus {

    /**
     * 响应成功
     */
    const val SUCCESS = 200
    /**
     * 未知错误
     */
    const val UNKNOWN_ERROR = 1000
    /**
     * 服务器内部错误
     */
    const val SERVER_ERROR = 1001
    /**
     * 网络连接超时
     */
    const val NETWORK_ERROR = 1002
    /**
     * 数据解析异常（或者第三方数据结构更改）等其他异常
     */
    const val DATA_PARSING_ERROR = 1003
    /**
     * 参数错误
     */
    const val PARAMS_ERROR = 1004
    /**
     * 证书验证错误
     */
    const val CERTIFICATE_VALIDATION_ERROR = 1005
}