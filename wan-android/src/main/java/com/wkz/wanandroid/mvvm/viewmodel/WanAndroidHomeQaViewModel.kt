package com.wkz.wanandroid.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.wkz.wanandroid.mvvm.model.WanAndroidPageBean

/**
 *  @desc: 首页问答ViewModel
 *  @date: 2019-11-06 11:11
 */
class WanAndroidHomeQaViewModel : WanAndroidBaseViewModel() {

    /* 页数 */
    private val mPage = MutableLiveData<Int>()

    /* 是否正在刷新 */
    val mRefreshing = MutableLiveData<Boolean>()

    /* 是否正在加载更多 */
    val mLoadingMore = MutableLiveData<Boolean>()

    /* 问答列表 */
    val mQaList = Transformations.switchMap(mPage) { page ->
        Transformations.map(sWanAndroidService.getQaList(page, 440)) {
            mRefreshing.value = false
            mLoadingMore.value = false
            it.data ?: WanAndroidPageBean(1, ArrayList(), 0, true, 1, 20, 0)
        }
    }

    fun autoRefresh() {
        mRefreshing.value = true
        mPage.value = 0
    }

    fun loadMore() {
        mLoadingMore.value = true
        mPage.value = (mPage.value ?: 0) + 1
    }
}