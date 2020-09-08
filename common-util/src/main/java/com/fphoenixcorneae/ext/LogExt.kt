package com.fphoenixcorneae.ext

import com.orhanobut.logger.Logger

fun loggerD(any: Any?) {
    Logger.d(any)
}

fun loggerD(message: String, tag: String? = null) {
    Logger.t(tag).d(message)
}

fun loggerE(message: String, tag: String? = null) {
    Logger.t(tag).e(message)
}

fun loggerI(message: String, tag: String? = null) {
    Logger.t(tag).i(message)
}

fun loggerW(message: String, tag: String? = null) {
    Logger.t(tag).w(message)
}

fun loggerJson(json: String?, tag: String? = null) {
    Logger.t(tag).json(json)
}

fun loggerXml(xml: String?, tag: String? = null) {
    Logger.t(tag).xml(xml)
}