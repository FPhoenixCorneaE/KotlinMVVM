package com.wkz.rxretrofit.network.exception

import com.google.gson.JsonParseException
import com.orhanobut.logger.Logger

import org.json.JSONException
import retrofit2.HttpException

import java.net.ConnectException

import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.text.ParseException
import javax.net.ssl.SSLException

/**
 * @desc: 异常处理类
 */
class ExceptionHandle {

    companion object {
        var errorCode: Int = ErrorStatus.UNKNOWN_ERROR
        var errorMsg: String = "请求失败，请稍后重试！"

        fun handleException(e: Throwable): String {
            when (e) {
                is SocketTimeoutException, is ConnectException, is UnknownHostException, is HttpException -> {
                    // 均视为网络错误
                    errorCode = ErrorStatus.NETWORK_ERROR
                    errorMsg = "网络连接异常！"
                }
                is JsonParseException, is JSONException, is ParseException -> {
                    // 均视为解析错误
                    errorCode = ErrorStatus.DATA_PARSING_ERROR
                    errorMsg = "数据解析异常！"
                }
                is ApiException -> {
                    // 服务器返回的错误信息
                    errorCode = ErrorStatus.SERVER_ERROR
                    errorMsg = e.message.toString()
                }
                is IllegalArgumentException -> {
                    // 参数错误
                    errorCode = ErrorStatus.PARAMS_ERROR
                    errorMsg = "参数错误！"
                }
                is SSLException -> {
                    // 证书验证错误
                    errorCode = ErrorStatus.CERTIFICATE_VALIDATION_ERROR
                    errorMsg = "证书验证错误！"
                }
                else -> {
                    // 未知错误
                    errorCode = ErrorStatus.UNKNOWN_ERROR
                    errorMsg = "未知错误，可能抛锚了吧~"
                }
            }

            Logger.e("errorCode:$errorCode errorMsg:$errorMsg")
            Logger.e("Exception:$e")
            return errorMsg
        }
    }
}
