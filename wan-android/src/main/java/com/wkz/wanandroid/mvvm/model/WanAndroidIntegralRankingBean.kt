package com.wkz.wanandroid.mvvm.model

import java.io.Serializable

/**
 * 积分排行榜
 */
data class WanAndroidIntegralRankingBean(
    var datas: MutableList<WanAndroidIntegralBean>? = mutableListOf(),
    var curPage: Int = 0,
    var offset: Int = 0,
    var over: Boolean = false,
    var pageCount: Int = 0,
    var size: Int = 0,
    var total: Int = 0
) : Serializable