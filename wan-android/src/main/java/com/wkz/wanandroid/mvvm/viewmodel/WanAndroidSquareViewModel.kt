package com.wkz.wanandroid.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.wkz.wanandroid.mvvm.model.WanAndroidUIState

/**
 *  @desc: 广场ViewModel
 *  @date: 2020-06-20 10:12
 */
class WanAndroidSquareViewModel : WanAndroidBaseViewModel() {

    /* 刷新广场分类 */
    val mRefreshingClassify = MutableLiveData<Boolean>()

    /* 广场数据UI状态 */
    val mDataUIState = WanAndroidUIState()

    /* 广场分类id */
    var mClassifyId = 0

    /* 广场分类 */
    val mVipcnClassify = Transformations.switchMap(mRefreshingClassify) {
        Transformations.map(sWanAndroidService.getVipcnClassify()) {
            it.data
        }
    }

    /* 广场数据 */
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
     * 获取广场分类
     */
    fun getVipcnClassify() {
        mRefreshingClassify.value = true
    }

    /**
     * 刷新广场数据
     * @param isNewest 是否是最新的
     */
    fun refreshVipcnData() {
        mDataUIState.mRefreshing.value = true
        mDataUIState.mPage.value = 1
    }

    /**
     * 加载更多广场数据
     * @param isNewest 是否是最新的
     */
    fun loadMoreVipcnData() {
        mDataUIState.mLoadingMore.value = true
        mDataUIState.mPage.value = (mDataUIState.mPage.value ?: 1) + 1
    }
}