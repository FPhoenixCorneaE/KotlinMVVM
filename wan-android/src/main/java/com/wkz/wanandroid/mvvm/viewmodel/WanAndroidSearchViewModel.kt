package com.wkz.wanandroid.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.google.gson.reflect.TypeToken
import com.wkz.util.SharedPreferencesUtil
import com.wkz.util.gson.GsonUtil
import com.wkz.wanandroid.constant.WanAndroidConstant
import com.wkz.wanandroid.mvvm.model.WanAndroidSearchBean
import com.wkz.wanandroid.mvvm.model.WanAndroidUIState

/**
 *  @desc: 搜索ViewModel
 *  @date: 2020-07-18 14:12
 */
class WanAndroidSearchViewModel : WanAndroidBaseViewModel() {

    /* 刷新搜索热词 */
    val mRefreshingHotSearch = MutableLiveData<Boolean>()

    /* 搜索数据UI状态 */
    val mSearchDataUIState = WanAndroidUIState()

    /* 搜索关键词 */
    var mSearchKey: String = ""

    /* 搜索历史 */
    val mSearchHistory = MutableLiveData<ArrayList<WanAndroidSearchBean>>()

    /* 搜索热词 */
    val mHotSearch = Transformations.switchMap(mRefreshingHotSearch) {
        Transformations.map(sWanAndroidService.getHotSearchData()) {
            it.data
        }
    }

    /* 搜索结果 */
    val mSearchData = Transformations.switchMap(mSearchDataUIState.mPage) { page ->
        Transformations.map(sWanAndroidService.getSearchDataByKey(page, mSearchKey)) {
            it.data
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
            GsonUtil.fromJson(
                SharedPreferencesUtil.getString(
                    WanAndroidConstant.WAN_ANDROID_SEARCH_HISTORY,
                    "[]"
                ), object : TypeToken<ArrayList<WanAndroidSearchBean>>() {}.type
            )
        )
    }

    /**
     * 刷新搜索结果
     * @param searchKey 关键词
     */
    fun refreshSearchDataByKey(searchKey: String) {
        mSearchKey = searchKey
        mSearchDataUIState.mPage.value = 0
    }

    /**
     * 加载更多搜索结果
     * @param searchKey 关键词
     */
    fun loadMoreSearchDataByKey(searchKey: String) {
        mSearchKey = searchKey
        mSearchDataUIState.mPage.value = (mSearchDataUIState.mPage.value ?: 0) + 1
    }
}