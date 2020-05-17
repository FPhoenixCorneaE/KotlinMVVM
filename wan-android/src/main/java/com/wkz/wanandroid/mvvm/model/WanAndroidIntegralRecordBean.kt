package com.wkz.wanandroid.mvvm.model

import java.io.Serializable

/**
 * 积分记录
 */
data class WanAndroidIntegralRecordBean(
    var datas: MutableList<IntegralRecordPageBean>,
    var curPage: Int,
    var offset: Int,
    var over: Boolean,
    var pageCount: Int,
    var size: Int,
    var total: Int
) : Serializable {

    data class IntegralRecordPageBean(
        var coinCount: Int,
        var date: Long,
        var desc: String,
        var id: Int,
        var type: Int,
        var reason: String,
        var userId: Int,
        var userName: String
    ) : Serializable
}


