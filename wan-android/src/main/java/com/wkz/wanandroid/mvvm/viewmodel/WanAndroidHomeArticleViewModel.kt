package com.wkz.wanandroid.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.orhanobut.logger.Logger
import com.wkz.wanandroid.mvvm.model.WanAndroidPageBean

/**
 *  @desc: 首页文章ViewModel
 *  @date: 2019-10-24 16:44
 */
class WanAndroidHomeArticleViewModel : WanAndroidBaseViewModel() {

    /* 页数 */
    val mPage = MutableLiveData<Int>()
    /* 文章列表 */
    val mArticleList = Transformations.switchMap(mPage) { it ->
        Transformations.map(sWanAndroidService.getArticleList(it)) {
            it.data ?: WanAndroidPageBean(1, ArrayList(), 0, true, 1, 20, 0)
        }
    }

    fun autoRefresh() {
        mPage.value = 0
    }
}