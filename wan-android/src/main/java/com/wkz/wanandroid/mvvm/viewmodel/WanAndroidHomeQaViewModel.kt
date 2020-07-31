package com.wkz.wanandroid.mvvm.viewmodel

import androidx.lifecycle.Transformations
import com.wkz.wanandroid.mvvm.model.WanAndroidUiState

/**
 *  @desc: 首页问答ViewModel
 *  @date: 2019-11-06 11:11
 */
class WanAndroidHomeQaViewModel : WanAndroidBaseViewModel() {

    /* 问答数据UI状态 */
    val mQaDataUIState = WanAndroidUiState()

    /* 问答列表 */
    val mQaList = Transformations.switchMap(mQaDataUIState.mPage) { page ->
        Transformations.map(sWanAndroidService.getQaList(page, 440)) {
            it?.setPageDataUiState(mQaDataUIState)
        }
    }

    fun autoRefresh() {
        mQaDataUIState.mRefreshing.value = true
        mQaDataUIState.mPage.value = 0
    }

    fun loadMore() {
        mQaDataUIState.mLoadingMore.value = true
        mQaDataUIState.mPage.value = (mQaDataUIState.mPage.value ?: 0) + 1
    }
}