package com.wkz.kotlinmvvm.mvvm.model

import com.wkz.kotlinmvvm.mvvm.model.bean.OpenEyesHomeBean
import com.wkz.rxretrofit.scheduler.SchedulerManager
import io.reactivex.Observable

/**
 * @desc: 关注 Model
 */
class OpenEyesFollowModel : OpenEyesBaseModel() {

    /**
     * 获取关注信息
     */
    fun requestFollowList(): Observable<OpenEyesHomeBean.Issue> {
        return sOpenEyesService
            .getFollowInfo()
            .compose(SchedulerManager.ioToMain())
    }

    /**
     * 加载更多
     */
    fun loadMoreData(url: String): Observable<OpenEyesHomeBean.Issue> {
        return sOpenEyesService
            .getIssueData(url)
            .compose(SchedulerManager.ioToMain())
    }
}
