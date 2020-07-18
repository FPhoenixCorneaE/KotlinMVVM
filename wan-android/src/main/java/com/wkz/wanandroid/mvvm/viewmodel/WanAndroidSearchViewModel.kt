package com.wkz.wanandroid.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations

/**
 *  @desc: 搜索ViewModel
 *  @date: 2020-07-18 14:12
 */
class WanAndroidSearchViewModel : WanAndroidBaseViewModel() {

    /* 刷新搜索热词 */
    val mRefreshingHotSearch = MutableLiveData<Boolean>()

    /* 搜索热词 */
    val mHotSearch = Transformations.switchMap(mRefreshingHotSearch) {
        Transformations.map(sWanAndroidService.getHotSearchData()) {
            it.data
        }
    }

    /**
     * 获取搜索热词
     */
    fun getHotSearchData() {
        mRefreshingHotSearch.value = true
    }
}