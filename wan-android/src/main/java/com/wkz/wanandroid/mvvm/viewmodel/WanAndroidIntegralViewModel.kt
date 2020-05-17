package com.wkz.wanandroid.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.wkz.wanandroid.mvvm.model.WanAndroidPageBean

/**
 *  @desc: 积分ViewModel
 *  @date: 2020-05-17 21:51
 */
class WanAndroidIntegralViewModel : WanAndroidBaseViewModel() {

    /* 页数 */
    private val mPage = MutableLiveData<Int>()

    /* 刷新积分 */
    val mRefreshIntegral = MutableLiveData<Boolean>()

    /* 是否正在加载更多 */
    val mLoadingMore = MutableLiveData<Boolean>()

    /* 用户积分 */
    val mUserIntegral = Transformations.switchMap(mRefreshIntegral) {
        Transformations.map(sWanAndroidService.getIntegral()) {
            mRefreshIntegral.value = false
            it.data
        }
    }

    /**
     * 获取当前账户的个人积分
     */
    fun getIntegral() {
        mRefreshIntegral.value = true
    }
    /**
     * 获取积分排行榜
     */
    fun getIntegralRanking() {
        mRefreshIntegral.value = true
    }

    fun loadMore() {
        mLoadingMore.value = true
        mPage.value = (mPage.value ?: 0) + 1
    }
}