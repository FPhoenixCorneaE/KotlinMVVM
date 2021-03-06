package com.fphoenixcorneae.wanandroid.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.fphoenixcorneae.util.SharedPreferencesUtil
import com.fphoenixcorneae.ext.gson.toObject
import com.fphoenixcorneae.wanandroid.constant.WanAndroidConstant
import com.fphoenixcorneae.wanandroid.mvvm.model.WanAndroidSearchBean
import com.fphoenixcorneae.wanandroid.mvvm.model.WanAndroidUiState
import com.google.gson.reflect.TypeToken

/**
 *  @desc: 搜索ViewModel
 *  @date: 2020-07-18 14:12
 */
class WanAndroidSearchViewModel : WanAndroidBaseViewModel() {

    /* 刷新搜索热词 */
    val mRefreshingHotSearch = MutableLiveData<Boolean>()

    /* 搜索数据UI状态 */
    val mSearchDataUIState = WanAndroidUiState()

    /* 搜索关键词 */
    var mSearchKey: String = ""

    /* 搜索历史 */
    val mSearchHistory = MutableLiveData<ArrayList<WanAndroidSearchBean>>()

    /* 搜索热词 */
    val mHotSearch = Transformations.switchMap(mRefreshingHotSearch) {
        Transformations.map(sWanAndroidService.getHotSearchData()) {
            it?.data
        }
    }

    /* 搜索结果 */
    val mSearchData = Transformations.switchMap(mSearchDataUIState.mPage) { page ->
        Transformations.map(sWanAndroidService.getSearchDataByKey(page, mSearchKey)) {
            it?.setPageDataUiState(mSearchDataUIState)
        }
    }

    /**
     * 获取搜索热词
     */
    fun getHotSearchData() {
        mRefreshingHotSearch.value = true
    }

    /**
     * 获取搜索历史
     */
    fun getSearchHistoryData() {
        mSearchHistory.postValue(
            SharedPreferencesUtil.getString(
                WanAndroidConstant.WAN_ANDROID_SEARCH_HISTORY,
                "[]"
            ).toObject(object : TypeToken<ArrayList<WanAndroidSearchBean>>() {}.type)
        )
    }

    /**
     * 刷新搜索结果
     * @param searchKey 关键词
     */
    fun refreshSearchDataByKey(searchKey: String) {
        mSearchKey = searchKey
        mSearchDataUIState.mRefreshing.value = true
        mSearchDataUIState.mPage.value = 0
    }

    /**
     * 加载更多搜索结果
     * @param searchKey 关键词
     */
    fun loadMoreSearchDataByKey(searchKey: String) {
        mSearchKey = searchKey
        mSearchDataUIState.mLoadingMore.value = true
        mSearchDataUIState.mPage.value = (mSearchDataUIState.mPage.value ?: 0) + 1
    }
}