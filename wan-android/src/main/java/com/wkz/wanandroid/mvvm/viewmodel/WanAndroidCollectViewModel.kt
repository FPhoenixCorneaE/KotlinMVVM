package com.wkz.wanandroid.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.wkz.wanandroid.mvvm.model.WanAndroidUiState

/**
 *  @desc: 收藏ViewModel
 *  @date: 2020-05-22 14:33
 */
class WanAndroidCollectViewModel : WanAndroidBaseViewModel() {

    /* 收藏文章id */
    private val mCollectArticleId = MutableLiveData<Int>()

    /* 取消收藏文章id */
    private val mCancelCollectArticleId = MutableLiveData<Int>()

    /* 收藏文章数据UI状态 */
    val mCollectArticleDataUIState = WanAndroidUiState()

    /* 收藏网址数据UI状态 */
    val mCollectWebsiteDataUIState = WanAndroidUiState()


    /* 收藏文章 */
    val mArticleCollect = Transformations.switchMap(mCollectArticleId) { id ->
        Transformations.map(sWanAndroidService.collectArticle(id)) {

        }
    }

    /* 取消收藏文章 */
    val mArticleCancelCollect = Transformations.switchMap(mCancelCollectArticleId) { id ->
        Transformations.map(sWanAndroidService.cancelCollectArticle(id)) {

        }
    }

    /* 收藏文章数据 */
    val mCollectArticleData = Transformations.switchMap(mCollectArticleDataUIState.mPage) { page ->
        Transformations.map(sWanAndroidService.getCollectArticleData(page)) {
            it?.setPageDataUiState(mCollectArticleDataUIState)
        }
    }

    /* 收藏网址数据 */
    val mCollectWebsiteData = Transformations.switchMap(mCollectWebsiteDataUIState.mRefreshing) {
        Transformations.map(sWanAndroidService.getCollectWebsiteData()) {

        }
    }

    /**
     * 收藏文章
     */
    fun collectArticle(id: Int) {
        mCollectArticleId.value = id
    }

    /**
     * 取消收藏文章
     */
    fun cancelCollectArticle(id: Int) {
        mCancelCollectArticleId.value = id
    }

    /**
     * 刷新收藏文章数据
     */
    fun refreshCollectArticleData() {
        mCollectArticleDataUIState.mRefreshing.value = true
        mCollectArticleDataUIState.mPage.value = 0
    }

    /**
     * 加载更多收藏文章数据
     */
    fun loadMoreCollectArticleData() {
        mCollectArticleDataUIState.mLoadingMore.value = true
        mCollectArticleDataUIState.mPage.value = (mCollectArticleDataUIState.mPage.value ?: 0) + 1
    }
}