package com.wkz.rxretrofit.network.exception

object ErrorStatus {

    /**
     * 响应成功
     */
    @JvmField
    val SUCCESS = 200

    /**
     * 服务器内部错误
     */
    @JvmField
    val SERVER_ERROR = 1001

    /**
     * 网络连接超时
     */
    @JvmField
    val NETWORK_ERROR = 1002

    /**
     * 数据解析异常（或者第三方数据结构更改）等其他异常
     */
    @JvmField
    val DATA_PARSING_ERROR = 1003

    /**
     * 参数错误
     */
    @JvmField
    val PARAMS_ERROR = 1004

    /**
     * 未知错误
     */
    @JvmField
    val UNKNOWN_ERROR = 1005
}