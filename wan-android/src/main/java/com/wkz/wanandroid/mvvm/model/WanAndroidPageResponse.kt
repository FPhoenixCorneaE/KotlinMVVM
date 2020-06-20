package com.wkz.wanandroid.mvvm.model

import java.io.Serializable

/**
 * @desc：分页数据的基类
 * @date：2020-06-11 10:20
 */
data class WanAndroidPageResponse<T>(
    var datas: T,
    var curPage: Int,
    var offset: Int,
    var over: Boolean,
    var pageCount: Int,
    var size: Int,
    var total: Int
) : Serializable {
    /**
     * 数据为空
     */
    fun isEmptyData(): Boolean {
        return (datas as List<*>).size == 0
    }

    /**
     * 数据不为空
     */
    fun isNotEmptyData(): Boolean {
        return !isEmptyData()
    }

    /**
     * 是否为刷新
     */
    fun isRefresh(): Boolean {
        // WanAndroid 第一页该字段都为0
        return offset == 0
    }

    /**
     * 是否为加载更多
     */
    fun isLoadMore(): Boolean {
        return offset != 0
    }

    /**
     * 是否为刷新没有数据
     */
    fun isRefreshNoData(): Boolean {
        return isRefresh() && isEmptyData()
    }

    /**
     * 是否为刷新有数据
     */
    fun isRefreshWithData(): Boolean {
        return isRefresh() && isNotEmptyData()
    }

    /**
     * 是否还有更多数据
     */
    fun isLoadMoreNoData(): Boolean {
        return isLoadMore() && over
    }
}
