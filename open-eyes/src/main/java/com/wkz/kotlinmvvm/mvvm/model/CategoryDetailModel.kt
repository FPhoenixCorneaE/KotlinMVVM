package com.wkz.kotlinmvvm.mvvm.model

import com.wkz.kotlinmvvm.mvvm.model.bean.HomeBean
import com.wkz.rxretrofit.scheduler.SchedulerManager
import io.reactivex.Observable

/**
 * @desc: 分类详情 Model
 */
class CategoryDetailModel : OpenEyesBaseModel() {

    /**
     * 获取分类下的 List 数据
     */
    fun getCategoryDetailList(id: Long): Observable<HomeBean.Issue> {
        return sOpenEyesService
            .getCategoryDetailList(id)
            .compose(SchedulerManager.ioToMain())
    }

    /**
     * 加载更多数据
     */
    fun loadMoreData(url: String): Observable<HomeBean.Issue> {
        return sOpenEyesService
            .getIssueData(url)
            .compose(SchedulerManager.ioToMain())
    }
}