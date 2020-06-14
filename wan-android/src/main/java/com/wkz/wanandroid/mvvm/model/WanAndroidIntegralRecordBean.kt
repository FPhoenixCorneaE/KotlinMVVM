package com.wkz.wanandroid.mvvm.model

import java.io.Serializable

/**
 * 积分记录
 */
data class WanAndroidIntegralRecordBean(
    var coinCount: Int = 0,
    var date: Long = 0,
    var desc: String = "",
    var id: Int = 0,
    var type: Int = 0,
    var reason: String = "",
    var userId: Int = 0,
    var userName: String = ""
) : Serializable


