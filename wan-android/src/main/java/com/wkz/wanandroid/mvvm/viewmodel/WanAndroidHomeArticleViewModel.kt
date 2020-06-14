package com.wkz.wanandroid.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations

/**
 *  @desc: 首页文章ViewModel
 *  @date: 2019-10-24 16:44
 */
class WanAndroidHomeArticleViewModel : WanAndroidBaseViewModel() {

    /* 页数 */
    private val mPage = MutableLiveData<Int>()

    /* 是否获取指定文章 */
    private val mAcquireTopArticle = MutableLiveData<Boolean>()

    /* 是否正在刷新 */
    val mRefreshing = MutableLiveData<Boolean>()

    /* 是否正在加载更多 */
    val mLoadingMore = MutableLiveData<Boolean>()

    /* Banner */
    val mBannerList = Transformations.switchMap(mPage) {
        Transformations.map(sWanAndroidService.getBannerList()) {
            it.data ?: arrayListOf()
        }
    }

    /* 置顶文章列表 */
    val mTopArticleList = Transformations.switchMap(mAcquireTopArticle) {
        Transformations.map(sWanAndroidService.getTopArticleList()) {
            mAcquireTopArticle.value = false
            it.data
        }
    }

    /* 文章列表 */
    val mArticleList = Transformations.switchMap(mPage) { page ->
        Transformations.map(sWanAndroidService.getArticleList(page)) {
            mRefreshing.value = false
            mLoadingMore.value = false
            it.data
        }
    }

    /**
     * 下拉刷新
     */
    fun autoRefresh() {
        mRefreshing.value = true
        mAcquireTopArticle.value = true
        mPage.value = 0
    }

    /**
     * 上拉加载更多
     */
    fun loadMore() {
        mLoadingMore.value = true
        mPage.value = (mPage.value ?: 0) + 1
    }
}