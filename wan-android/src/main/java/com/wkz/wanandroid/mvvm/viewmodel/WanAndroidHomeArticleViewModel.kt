package com.wkz.wanandroid.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations

/**
 *  @desc: 首页文章ViewModel
 *  @date: 2019-10-24 16:44
 */
class WanAndroidHomeArticleViewModel : WanAndroidBaseViewModel() {

    /* 页数 */
    private val mPage = MutableLiveData<Int>()
    /* 文章列表 */
    private val mArtivleList = Transformations.switchMap(mPage) {
        sWanAndroidService.getArticleList(it)
    }
}