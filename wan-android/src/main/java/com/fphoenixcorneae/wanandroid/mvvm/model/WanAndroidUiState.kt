package com.fphoenixcorneae.wanandroid.mvvm.model

import androidx.lifecycle.MutableLiveData

/**
 * @desc：用户界面状态
 * @date：2020-06-11 10:33
 */
data class WanAndroidUiState(

    /* 页数 */
    val mPage: MutableLiveData<Int> = MutableLiveData(),

    /* 是否正在刷新 */
    val mRefreshing: MutableLiveData<Boolean> = MutableLiveData(),

    /* 是否正在加载更多 */
    val mLoadingMore: MutableLiveData<Boolean> = MutableLiveData(),

    /* 刷新数据成功 */
    val mRefreshSuccess: MutableLiveData<Boolean> = MutableLiveData(),

    /* 加载更多数据成功 */
    val mLoadMoreSuccess: MutableLiveData<Boolean> = MutableLiveData(),

    /* 刷新后没数据 */
    val mRefreshNoData: MutableLiveData<Boolean> = MutableLiveData(),

    /* 加载更多后没数据 */
    val mLoadMoreNoData: MutableLiveData<Boolean> = MutableLiveData(),

    /* 错误消息 mRefreshSuccess/mLoadMoreSuccess为false才会有 */
    val mErrorMessage: MutableLiveData<String> = MutableLiveData()
)