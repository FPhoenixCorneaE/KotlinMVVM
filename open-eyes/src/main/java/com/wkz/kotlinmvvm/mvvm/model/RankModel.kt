package com.wkz.kotlinmvvm.mvvm.model

import com.wkz.kotlinmvvm.mvvm.model.bean.HomeBean
import com.wkz.rxretrofit.scheduler.SchedulerManager
import io.reactivex.Observable

/**
 * @desc: 排行榜 Model
 */
class RankModel : OpenEyesBaseModel(){

    /**
     * 获取排行榜
     */
    fun requestRankList(apiUrl:String): Observable<HomeBean.Issue> {
        return sOpenEyesService.getIssueData(apiUrl)
                .compose(SchedulerManager.ioToMain())
    }

}
