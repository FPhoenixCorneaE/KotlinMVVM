package com.wkz.wanandroid.mvvm.model

import java.io.Serializable

/**
 * 积分排行榜
 */
data class WanAndroidIntegralRankingBean(
    var datas: MutableList<WanAndroidIntegralBean>,
    var curPage: Int,
    var offset: Int,
    var over: Boolean,
    var pageCount: Int,
    var size: Int,
    var total: Int
) : Serializable