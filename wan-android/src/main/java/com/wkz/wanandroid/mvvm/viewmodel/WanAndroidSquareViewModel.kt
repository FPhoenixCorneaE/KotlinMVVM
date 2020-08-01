package com.wkz.wanandroid.mvvm.viewmodel

import androidx.lifecycle.Transformations
import com.wkz.extension.isNonNullAndNotEmpty
import com.wkz.wanandroid.mvvm.model.WanAndroidUiState

/**
 *  @desc: 广场ViewModel
 *  @date: 2020-06-20 10:12
 */
class WanAndroidSquareViewModel : WanAndroidBaseViewModel() {

    /* 广场文章数据UI状态 */
    val mArticleDataUIState = WanAndroidUiState()

    /* 广场问答数据UI状态 */
    val mAskDataUIState = WanAndroidUiState()

    /* 广场体系数据UI状态 */
    val mSystemDataUIState = WanAndroidUiState()

    /* 广场体系文章数据UI状态 */
    val mSystemArticleDataUIState = WanAndroidUiState()

    /* 广场体系id */
    var mSystemId = 0

    /* 广场导航数据UI状态 */
    val mNavigationDataUIState = WanAndroidUiState()

    /* 广场文章数据 */
    val mSquareArticleData =
        Transformations.switchMap(mArticleDataUIState.mPage) { page ->
            Transformations.map(sWanAndroidService.getSquareArticleList(page)) {
                it?.setPageDataUiState(mArticleDataUIState)
            }
        }

    /* 广场问答数据 */
    val mSquareAskData =
        Transformations.switchMap(mAskDataUIState.mPage) { page ->
            Transformations.map(sWanAndroidService.getSquareAskList(page)) {
                it?.setPageDataUiState(mAskDataUIState)
            }
        }

    /* 广场体系数据 */
    val mSquareSystemData = Transformations.switchMap(mSystemDataUIState.mPage) {
        Transformations.map(sWanAndroidService.getSquareSystem()) {
            it.setArrayListDataUiState(mSystemDataUIState)
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
                it?.setPageDataUiState(mSystemArticleDataUIState)
            }
        }

    /* 广场导航数据 */
    val mSquareNavigationData = Transformations.switchMap(mNavigationDataUIState.mPage) {
        Transformations.map(sWanAndroidService.getSquareNavigation()) {
            it.setArrayListDataUiState(mNavigationDataUIState)
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
        mSystemDataUIState.mPage.postValue(0)
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
        mNavigationDataUIState.mPage.postValue(0)
    }
}