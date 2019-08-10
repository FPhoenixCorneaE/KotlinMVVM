package com.wkz.kotlinmvvm.mvp.model

import com.wkz.kotlinmvvm.mvp.model.bean.HomeBean
import com.wkz.rxretrofit.scheduler.SchedulerManager
import io.reactivex.Observable

/**
 * @desc: 关注 Model
 */
class FollowModel : OpenEyesBaseModel() {

    /**
     * 获取关注信息
     */
    fun requestFollowList(): Observable<HomeBean.Issue> {
        return sOpenEyesService
            .getFollowInfo()
            .compose(SchedulerManager.ioToMain())
    }

    /**
     * 加载更多
     */
    fun loadMoreData(url: String): Observable<HomeBean.Issue> {
        return sOpenEyesService
            .getIssueData(url)
            .compose(SchedulerManager.ioToMain())
    }
}
