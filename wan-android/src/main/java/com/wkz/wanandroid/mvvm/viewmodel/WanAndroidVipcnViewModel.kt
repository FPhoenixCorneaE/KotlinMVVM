package com.wkz.wanandroid.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.wkz.wanandroid.mvvm.model.WanAndroidUIState

/**
 *  @desc: 公众号ViewModel
 *  @date: 2020-06-18 17:40
 */
class WanAndroidVipcnViewModel : WanAndroidBaseViewModel() {

    /* 刷新公众号分类 */
    val mRefreshingClassify = MutableLiveData<Boolean>()

    /* 公众号数据UI状态 */
    val mDataUIState = WanAndroidUIState()

    /* 公众号分类id */
    var mClassifyId = 0

    /* 公众号分类 */
    val mVipcnClassify = Transformations.switchMap(mRefreshingClassify) {
        Transformations.map(sWanAndroidService.getVipcnClassify()) {
            it.data
        }
    }

    /* 公众号数据 */
    val mVipcnData = Transformations.switchMap(mDataUIState.mPage) { page ->
        Transformations.map(sWanAndroidService.getVipcnDataByClassifyId(page, mClassifyId)) {
            mDataUIState.mRefreshing.value = false
            mDataUIState.mLoadingMore.value = false
            it.data?.apply {
                when {
                    it.isWanAndroidSuccess() -> {
                        when {
                            isRefreshNoData() -> {
                                mDataUIState.mRefreshNoData.value = true
                            }
                            isRefreshWithData() -> {
                                mDataUIState.mRefreshSuccess.value = true
                                when {
                                    isLoadMoreNoData() -> {
                                        mDataUIState.mLoadMoreNoData.value = true
                                    }
                                }
                            }
                            isLoadMoreNoData() -> {
                                mDataUIState.mLoadMoreNoData.value = true
                            }
                            else -> {
                                mDataUIState.mLoadMoreSuccess.value = true
                            }
                        }
                    }
                    isRefresh() -> {
                        mDataUIState.mRefreshSuccess.value = false
                    }
                    isLoadMore() -> {
                        mDataUIState.mLoadMoreSuccess.value = false
                    }
                }
            }
        }
    }

    /**
     * 获取公众号分类
     */
    fun getVipcnClassify() {
        mRefreshingClassify.value = true
    }

    /**
     * 刷新公众号数据
     */
    fun refreshVipcnData() {
        mDataUIState.mRefreshing.value = true
        mDataUIState.mPage.value = 1
    }

    /**
     * 加载更多公众号数据
     */
    fun loadMoreVipcnData() {
        mDataUIState.mLoadingMore.value = true
        mDataUIState.mPage.value = (mDataUIState.mPage.value ?: 1) + 1
    }
}