package com.wkz.wanandroid.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.wkz.wanandroid.mvvm.model.WanAndroidUIState

/**
 *  @desc: 广场ViewModel
 *  @date: 2020-06-20 10:12
 */
class WanAndroidSquareViewModel : WanAndroidBaseViewModel() {

    /* 刷新广场体系 */
    val mRefreshingSquareSystem = MutableLiveData<Boolean>()

    /* 广场体系文章数据UI状态 */
    val mSystemArticleDataUIState = WanAndroidUIState()

    /* 广场体系id */
    var mSystemId = 0

    /* 广场体系数据 */
    val mSquareSystemData = Transformations.switchMap(mRefreshingSquareSystem) {
        Transformations.map(sWanAndroidService.getSquareSystem()) {
            it.data
        }
    }

    /* 广场体系文章数据 */
    val mSquareSystemArticleData =
        Transformations.switchMap(mSystemArticleDataUIState.mPage) { page ->
            Transformations.map(
                sWanAndroidService.getSquareSystemArticleBySystemId(
                    page,
                    mSystemId
                )
            ) {
                mSystemArticleDataUIState.mRefreshing.value = false
                mSystemArticleDataUIState.mLoadingMore.value = false
                it.data?.apply {
                    when {
                        it.isWanAndroidSuccess() -> {
                            when {
                                isRefreshNoData() -> {
                                    mSystemArticleDataUIState.mRefreshNoData.value = true
                                }
                                isRefreshWithData() -> {
                                    mSystemArticleDataUIState.mRefreshSuccess.value = true
                                }
                                isLoadMoreNoData() -> {
                                    mSystemArticleDataUIState.mLoadMoreNoData.value = true
                                }
                                else -> {
                                    mSystemArticleDataUIState.mLoadMoreSuccess.value = true
                                }
                            }
                        }
                        isRefresh() -> {
                            mSystemArticleDataUIState.mRefreshSuccess.value = false
                        }
                        isLoadMore() -> {
                            mSystemArticleDataUIState.mLoadMoreSuccess.value = false
                        }
                    }
                }
            }
        }

    /**
     * 获取广场体系
     */
    fun getSquareSystem() {
        mRefreshingSquareSystem.value = true
    }

    /**
     * 刷新广场体系文章数据
     */
    fun refreshSquareSystemArticleData() {
        mSystemArticleDataUIState.mRefreshing.value = true
        mSystemArticleDataUIState.mPage.value = 1
    }

    /**
     * 加载更多广场体系文章数据
     */
    fun loadMoreSquareSystemArticleData() {
        mSystemArticleDataUIState.mLoadingMore.value = true
        mSystemArticleDataUIState.mPage.value = (mSystemArticleDataUIState.mPage.value ?: 1) + 1
    }
}