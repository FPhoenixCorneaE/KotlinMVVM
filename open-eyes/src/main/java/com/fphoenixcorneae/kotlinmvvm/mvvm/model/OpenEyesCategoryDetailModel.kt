package com.fphoenixcorneae.kotlinmvvm.mvvm.model

import com.fphoenixcorneae.kotlinmvvm.mvvm.model.bean.OpenEyesHomeBean
import com.fphoenixcorneae.rxretrofit.scheduler.SchedulerManager
import io.reactivex.Observable

/**
 * @desc: 分类详情 Model
 */
class OpenEyesCategoryDetailModel : OpenEyesBaseModel() {

    /**
     * 获取分类下的 List 数据
     */
    fun getCategoryDetailList(id: Long): Observable<OpenEyesHomeBean.Issue> {
        return sOpenEyesService
            .getCategoryDetailList(id)
            .compose(SchedulerManager.ioToMain())
    }

    /**
     * 加载更多数据
     */
    fun loadMoreData(url: String): Observable<OpenEyesHomeBean.Issue> {
        return sOpenEyesService
            .getIssueData(url)
            .compose(SchedulerManager.ioToMain())
    }
}