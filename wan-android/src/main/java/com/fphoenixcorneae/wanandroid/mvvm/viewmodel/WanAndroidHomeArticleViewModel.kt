package com.fphoenixcorneae.wanandroid.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.fphoenixcorneae.wanandroid.mvvm.model.WanAndroidUiState

/**
 *  @desc: 首页文章ViewModel
 *  @date: 2019-10-24 16:44
 */
class WanAndroidHomeArticleViewModel : WanAndroidBaseViewModel() {

    /* 是否获取Banner */
    private val mAcquireBanner = MutableLiveData<Boolean>()

    /* 是否获取置顶文章 */
    private val mAcquireTopArticle = MutableLiveData<Boolean>()

    /* 文章数据UI状态 */
    val mArticleDataUIState = WanAndroidUiState()

    /* Banner */
    val mBannerList = Transformations.switchMap(mAcquireBanner) {
        Transformations.map(sWanAndroidService.getBannerList()) {
            it?.data
        }
    }

    /* 置顶文章列表 */
    val mTopArticleList = Transformations.switchMap(mAcquireTopArticle) {
        Transformations.map(sWanAndroidService.getTopArticleList()) {
            it?.data
        }
    }

    /* 文章列表 */
    val mArticleList = Transformations.switchMap(mArticleDataUIState.mPage) { page ->
        Transformations.map(sWanAndroidService.getArticleList(page)) {
            it?.setPageDataUiState(mArticleDataUIState)
        }
    }

    /**
     * 下拉刷新
     */
    fun autoRefresh() {
        mAcquireBanner.value = true
        mAcquireTopArticle.value = true
        mArticleDataUIState.mRefreshing.value = true
        mArticleDataUIState.mPage.value = 0
    }

    /**
     * 上拉加载更多
     */
    fun loadMore() {
        mArticleDataUIState.mLoadingMore.value = true
        mArticleDataUIState.mPage.value = (mArticleDataUIState.mPage.value ?: 0) + 1
    }
}