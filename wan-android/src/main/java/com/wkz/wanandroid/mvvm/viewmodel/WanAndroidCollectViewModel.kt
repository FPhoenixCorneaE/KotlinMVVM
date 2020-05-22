package com.wkz.wanandroid.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations

/**
 *  @desc: 收藏文章、网址ViewModel
 *  @date: 2020-05-22 14:33
 */
class WanAndroidCollectViewModel : WanAndroidBaseViewModel() {

    /* 页数 */
    private val mPage = MutableLiveData<Int>()

    /* 是否正在刷新 */
    val mRefreshing = MutableLiveData<Boolean>()

    /* 是否正在加载更多 */
    val mLoadingMore = MutableLiveData<Boolean>()

    /* 收藏文章id */
    private val mCollectArticleId = MutableLiveData<Int>()

    /* 取消收藏文章id */
    private val mCancelCollectArticleId = MutableLiveData<Int>()

    /* 收藏文章/取消收藏文章 */
    private val mIsCollectArticle = MutableLiveData<Boolean>()


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

    /**
     * 下拉刷新
     */
    fun autoRefresh() {
        mRefreshing.value = true
        mPage.value = 0
    }

    /**
     * 上拉加载更多
     */
    fun loadMore() {
        mLoadingMore.value = true
        mPage.value = (mPage.value ?: 0) + 1
    }

    /**
     * 收藏文章
     */
    fun collectArticle(id: Int) {
        mCollectArticleId.value = id
        mIsCollectArticle.value = true
    }

    /**
     * 取消收藏文章
     */
    fun cancelCollectArticle(id: Int) {
        mCancelCollectArticleId.value = id
        mIsCollectArticle.value = false
    }
}