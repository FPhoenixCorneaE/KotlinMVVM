package com.wkz.wanandroid.mvvm.viewmodel

import androidx.lifecycle.Transformations
import com.wkz.wanandroid.mvvm.model.WanAndroidUIState

/**
 *  @desc: 广场ViewModel
 *  @date: 2020-06-20 10:12
 */
class WanAndroidSquareViewModel : WanAndroidBaseViewModel() {

    /* 广场文章数据UI状态 */
    val mArticleDataUIState = WanAndroidUIState()

    /* 广场问答数据UI状态 */
    val mAskDataUIState = WanAndroidUIState()

    /* 广场体系数据UI状态 */
    val mSystemDataUIState = WanAndroidUIState()

    /* 广场体系文章数据UI状态 */
    val mSystemArticleDataUIState = WanAndroidUIState()

    /* 广场体系id */
    var mSystemId = 0

    /* 广场导航数据UI状态 */
    val mNavigationDataUIState = WanAndroidUIState()

    /* 广场文章数据 */
    val mSquareArticleData =
        Transformations.switchMap(mArticleDataUIState.mPage) { page ->
            Transformations.map(sWanAndroidService.getSquareArticleList(page)) {
                mArticleDataUIState.mRefreshing.value = false
                mArticleDataUIState.mLoadingMore.value = false
                it.data?.apply {
                    when {
                        it.isWanAndroidSuccess() -> {
                            when {
                                isRefreshNoData() -> {
                                    mArticleDataUIState.mRefreshNoData.value = true
                                }
                                isRefreshWithData() -> {
                                    mArticleDataUIState.mRefreshSuccess.value = true
                                    when {
                                        isLoadMoreNoData() -> {
                                            mArticleDataUIState.mLoadMoreNoData.value = true
                                        }
                                    }
                                }
                                isLoadMoreNoData() -> {
                                    mArticleDataUIState.mLoadMoreNoData.value = true
                                }
                                else -> {
                                    mArticleDataUIState.mLoadMoreSuccess.value = true
                                }
                            }
                        }
                        isRefresh() -> {
                            mArticleDataUIState.mRefreshSuccess.value = false
                        }
                        isLoadMore() -> {
                            mArticleDataUIState.mLoadMoreSuccess.value = false
                        }
                    }
                }
            }
        }

    /* 广场问答数据 */
    val mSquareAskData =
        Transformations.switchMap(mAskDataUIState.mPage) { page ->
            Transformations.map(sWanAndroidService.getSquareAskList(page)) {
                mAskDataUIState.mRefreshing.value = false
                mAskDataUIState.mLoadingMore.value = false
                it.data?.apply {
                    when {
                        it.isWanAndroidSuccess() -> {
                            when {
                                isRefreshNoData() -> {
                                    mAskDataUIState.mRefreshNoData.value = true
                                }
                                isRefreshWithData() -> {
                                    mAskDataUIState.mRefreshSuccess.value = true
                                    when {
                                        isLoadMoreNoData() -> {
                                            mAskDataUIState.mLoadMoreNoData.value = true
                                        }
                                    }
                                }
                                isLoadMoreNoData() -> {
                                    mAskDataUIState.mLoadMoreNoData.value = true
                                }
                                else -> {
                                    mAskDataUIState.mLoadMoreSuccess.value = true
                                }
                            }
                        }
                        isRefresh() -> {
                            mAskDataUIState.mRefreshSuccess.value = false
                        }
                        isLoadMore() -> {
                            mAskDataUIState.mLoadMoreSuccess.value = false
                        }
                    }
                }
            }
        }

    /* 广场体系数据 */
    val mSquareSystemData = Transformations.switchMap(mSystemDataUIState.mRefreshing) {
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
                                    when {
                                        isLoadMoreNoData() -> {
                                            mSystemArticleDataUIState.mLoadMoreNoData.value = true
                                        }
                                    }
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

    /* 广场导航数据 */
    val mSquareNavigationData = Transformations.switchMap(mNavigationDataUIState.mRefreshing) {
        Transformations.map(sWanAndroidService.getSquareNavigation()) {
            it.data
        }
    }

    /**
     * 刷新广场文章数据
     */
    fun refreshSquareArticleData() {
        mArticleDataUIState.mRefreshing.value = true
        mArticleDataUIState.mPage.value = 0
    }

    /**
     * 加载更多广场文章数据
     */
    fun loadMoreSquareArticleData() {
        mArticleDataUIState.mLoadingMore.value = true
        mArticleDataUIState.mPage.value = (mArticleDataUIState.mPage.value ?: 0) + 1
    }

    /**
     * 刷新广场问答数据
     */
    fun refreshSquareAskData() {
        mAskDataUIState.mRefreshing.value = true
        mAskDataUIState.mPage.value = 1
    }

    /**
     * 加载更多广场问答数据
     */
    fun loadMoreSquareAskData() {
        mAskDataUIState.mLoadingMore.value = true
        mAskDataUIState.mPage.value = (mAskDataUIState.mPage.value ?: 1) + 1
    }

    /**
     * 获取广场体系
     */
    fun getSquareSystem() {
        mSystemDataUIState.mRefreshing.value = true
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

    /**
     * 获取广场导航
     */
    fun getSquareNavigation() {
        mNavigationDataUIState.mRefreshing.value = true
    }
}