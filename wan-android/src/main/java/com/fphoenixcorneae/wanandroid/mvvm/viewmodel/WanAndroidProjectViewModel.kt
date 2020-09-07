package com.fphoenixcorneae.wanandroid.mvvm.viewmodel

import androidx.lifecycle.Transformations
import com.fphoenixcorneae.wanandroid.mvvm.model.WanAndroidUiState

/**
 *  @desc: 项目ViewModel
 *  @date: 2020-06-16 10:45
 */
class WanAndroidProjectViewModel : WanAndroidBaseViewModel() {

    /* 项目分类数据UI状态 */
    val mClassifyDataUIState = WanAndroidUiState()

    /* 最新项目数据UI状态 */
    val mNewestDataUIState = WanAndroidUiState()

    /* 项目数据UI状态 */
    val mDataUIState = WanAndroidUiState()

    /* 项目分类id */
    var mClassifyId = 0

    /* 项目分类 */
    val mProjectClassify = Transformations.switchMap(mClassifyDataUIState.mPage) {
        Transformations.map(sWanAndroidService.getProjectClassify()) {
            it.setArrayListDataUiState(mClassifyDataUIState)
        }
    }

    /* 最新项目数据 */
    val mProjectNewestData = Transformations.switchMap(mNewestDataUIState.mPage) { page ->
        Transformations.map(sWanAndroidService.getProjectNewestData(page)) {
            it?.setPageDataUiState(mNewestDataUIState)
        }
    }

    /* 项目数据 */
    val mProjectData = Transformations.switchMap(mDataUIState.mPage) { page ->
        Transformations.map(sWanAndroidService.getProjectDataByClassifyId(page, mClassifyId)) {
            it?.setPageDataUiState(mDataUIState)
        }
    }

    /**
     * 获取项目分类
     */
    fun getProjectClassify() {
        mClassifyDataUIState.mPage.postValue(0)
    }

    /**
     * 刷新项目数据
     * @param isNewest 是否是最新的
     */
    fun refreshProjectData(isNewest: Boolean = false) {
        when {
            isNewest -> {
                mNewestDataUIState.mRefreshing.value = true
                mNewestDataUIState.mPage.value = 0
            }
            else -> {
                mDataUIState.mRefreshing.value = true
                mDataUIState.mPage.value = 1
            }
        }
    }

    /**
     * 加载更多项目数据
     * @param isNewest 是否是最新的
     */
    fun loadMoreProjectData(isNewest: Boolean = false) {
        when {
            isNewest -> {
                mNewestDataUIState.mLoadingMore.value = true
                mNewestDataUIState.mPage.value = (mNewestDataUIState.mPage.value ?: 0) + 1
            }
            else -> {
                mDataUIState.mLoadingMore.value = true
                mDataUIState.mPage.value = (mDataUIState.mPage.value ?: 1) + 1
            }
        }
    }
}