package com.wkz.adapter.bean

/**
 * 加载更多实体类
 */
data class LoadMoreBean(var loadState: Int = STATUS_NO_MORE) {
    companion object {
        const val STATUS_LOADING = 101
        const val STATUS_COMPLETED = 102
        const val STATUS_NO_MORE = 103
        const val STATUS_FAILURE = 104
    }
}
