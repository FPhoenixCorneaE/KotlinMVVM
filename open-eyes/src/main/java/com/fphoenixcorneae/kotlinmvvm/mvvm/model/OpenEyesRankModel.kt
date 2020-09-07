package com.fphoenixcorneae.kotlinmvvm.mvvm.model

import com.fphoenixcorneae.kotlinmvvm.mvvm.model.bean.OpenEyesHomeBean
import com.fphoenixcorneae.rxretrofit.scheduler.SchedulerManager
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
