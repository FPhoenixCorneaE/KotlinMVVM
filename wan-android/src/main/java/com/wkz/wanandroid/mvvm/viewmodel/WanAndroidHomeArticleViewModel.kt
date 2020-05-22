package com.wkz.wanandroid.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.wkz.wanandroid.mvvm.model.WanAndroidPageBean

/**
 *  @desc: 首页文章ViewModel
 *  @date: 2019-10-24 16:44
 */
class WanAndroidHomeArticleViewModel : WanAndroidBaseViewModel() {

    /* 页数 */
    private val mPage = MutableLiveData<Int>()

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
    val mTopArticleList = Transformations.switchMap(mPage) {
        Transformations.map(sWanAndroidService.getTopArticleList()) {
            if (mPage.value == 0) {
                it.data ?: arrayListOf()
            } else {
                arrayListOf()
            }
        }
    }

    /* 文章列表 */
    val mArticleList = Transformations.switchMap(mPage) { page ->
        Transformations.map(sWanAndroidService.getArticleList(page)) {
            mRefreshing.value = false
            mLoadingMore.value = false
            it.data ?: WanAndroidPageBean(1, arrayListOf(), 0, true, 1, 20, 0)
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
}