package com.wkz.wanandroid.mvvm.model

import java.io.Serializable

/**
 * 积分
 */
data class WanAndroidIntegralBean(
    var coinCount: Int,//当前积分
    var rank: Int,//当前排名
    var userId: Int,
    var username: String
) : Serializable


