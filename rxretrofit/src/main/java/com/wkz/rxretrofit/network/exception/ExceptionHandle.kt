package com.wkz.rxretrofit.network.exception

import com.google.gson.JsonParseException
import com.orhanobut.logger.Logger

import org.json.JSONException

import java.net.ConnectException

import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.text.ParseException

/**
 * @desc: 异常处理类
 */
class ExceptionHandle {

    companion object {
        var errorCode: Int = ErrorStatus.UNKNOWN_ERROR
        var errorMsg: String = "请求失败，请稍后重试！"

        fun handleException(e: Throwable): String {
            if (e is SocketTimeoutException
                || e is ConnectException
                || e is UnknownHostException
            ) {
                // 均视为网络错误
                errorCode = ErrorStatus.NETWORK_ERROR
                errorMsg = "网络连接异常！"
            } else if (e is JsonParseException
                || e is JSONException
                || e is ParseException
            ) {
                // 均视为解析错误
                errorCode = ErrorStatus.DATA_PARSING_ERROR
                errorMsg = "数据解析异常！"
            } else if (e is ApiException) {
                // 服务器返回的错误信息
                errorMsg = e.message.toString()
                errorCode = ErrorStatus.SERVER_ERROR
            } else if (e is IllegalArgumentException) {
                // 参数错误
                errorCode = ErrorStatus.PARAMS_ERROR
                errorMsg = "参数错误！"
            } else {
                // 未知错误
                errorCode = ErrorStatus.UNKNOWN_ERROR
                errorMsg = "未知错误，可能抛锚了吧~"
            }

            Logger.e("errorCode:$errorCode errorMsg:$errorMsg")
            Logger.e("Exception:$e")
            return errorMsg
        }
    }
}
