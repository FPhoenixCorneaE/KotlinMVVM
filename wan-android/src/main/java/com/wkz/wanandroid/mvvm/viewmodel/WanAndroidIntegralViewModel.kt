package com.wkz.wanandroid.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.wkz.wanandroid.mvvm.model.WanAndroidIntegralBean
import com.wkz.wanandroid.mvvm.model.WanAndroidUiState

/**
 *  @desc: 积分ViewModel
 *  @date: 2020-05-17 21:51
 */
class WanAndroidIntegralViewModel : WanAndroidBaseViewModel() {

    /* 排行榜页数 */
    private val mRankingPage = MutableLiveData<Int>()

    /* 刷新积分 */
    val mRefreshingIntegral = MutableLiveData<Boolean>()

    /* 刷新积分排行榜 */
    val mRefreshingIntegralRanking = MutableLiveData<Boolean>()

    /* 加载更多积分排行榜 */
    val mLoadingMoreIntegralRanking = MutableLiveData<Boolean>()

    /* 积分记录UI状态 */
    val mIntegralRecordUIState = WanAndroidUiState()

    /* 用户积分 */
    val mUserIntegral = Transformations.switchMap(mRefreshingIntegral) {
        Transformations.map(sWanAndroidService.getIntegral()) {
            it.data ?: WanAndroidIntegralBean()
        }
    }

    /* 积分排行榜 */
    val mIntegralRanking = Transformations.switchMap(mRankingPage) { page ->
        Transformations.map(sWanAndroidService.getIntegralRanking(page)) {
            mRefreshingIntegralRanking.value = false
            mLoadingMoreIntegralRanking.value = false
            it.data
        }
    }

    /* 积分记录 */
    val mIntegralRecord = Transformations.switchMap(mIntegralRecordUIState.mPage) { page ->
        Transformations.map(sWanAndroidService.getIntegralRecord(page)) {
            it.setPageDataUiState(mIntegralRecordUIState)
        }
    }

    /**
     * 获取当前账户的个人积分
     */
    fun getIntegral() {
        mRefreshingIntegral.value = true
    }

    /**
     * 刷新积分排行榜
     */
    fun refreshIntegralRanking() {
        mRefreshingIntegralRanking.value = true
        mRankingPage.value = 1
    }

    /**
     * 加载更多积分排行榜
     */
    fun loadMoreIntegralRanking() {
        mLoadingMoreIntegralRanking.value = true
        mRankingPage.value = (mRankingPage.value ?: 0) + 1
    }

    /**
     * 刷新积分记录
     */
    fun refreshIntegralRecord() {
        mIntegralRecordUIState.mRefreshing.value = true
        mIntegralRecordUIState.mPage.value = 1
    }

    /**
     * 加载更多积分记录
     */
    fun loadMoreIntegralRecord() {
        mIntegralRecordUIState.mLoadingMore.value = true
        mIntegralRecordUIState.mPage.value = (mIntegralRecordUIState.mPage.value ?: 1) + 1
    }
}