package com.wkz.wanandroid.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.wkz.wanandroid.mvvm.model.WanAndroidUiState

/**
 *  @desc: 公众号ViewModel
 *  @date: 2020-06-18 17:40
 */
class WanAndroidVipcnViewModel : WanAndroidBaseViewModel() {

    /* 刷新公众号分类 */
    val mRefreshingClassify = MutableLiveData<Boolean>()

    /* 公众号数据UI状态 */
    val mDataUIState = WanAndroidUiState()

    /* 公众号分类id */
    var mClassifyId = 0

    /* 公众号分类 */
    val mVipcnClassify = Transformations.switchMap(mRefreshingClassify) {
        Transformations.map(sWanAndroidService.getVipcnClassify()) {
            it?.data
        }
    }

    /* 公众号数据 */
    val mVipcnData = Transformations.switchMap(mDataUIState.mPage) { page ->
        Transformations.map(sWanAndroidService.getVipcnDataByClassifyId(page, mClassifyId)) {
            it?.setPageDataUiState(mDataUIState)
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