package com.wkz.wanandroid.mvvm.model

import java.io.Serializable

/**
 * 积分
 */
data class WanAndroidIntegralBean(
    var coinCount: Int = 0,//当前积分
    var rank: Int = 0,//当前排名
    var userId: Int = 0,
    var username: String = ""
) : Serializable


