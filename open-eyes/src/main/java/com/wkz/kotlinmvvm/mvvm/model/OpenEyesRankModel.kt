package com.wkz.kotlinmvvm.mvvm.model

import com.wkz.kotlinmvvm.mvvm.model.bean.OpenEyesHomeBean
import com.wkz.rxretrofit.scheduler.SchedulerManager
import io.reactivex.Observable

/**
 * @desc: 排行榜 Model
 */
class OpenEyesRankModel : OpenEyesBaseModel(){

    /**
     * 获取排行榜
     */
    fun requestRankList(apiUrl:String): Observable<OpenEyesHomeBean.Issue> {
        return sOpenEyesService.getIssueData(apiUrl)
                .compose(SchedulerManager.ioToMain())
    }

}
