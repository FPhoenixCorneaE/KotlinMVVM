package com.fphoenixcorneae.kotlinmvvm.mvvm.model

import com.fphoenixcorneae.kotlinmvvm.mvvm.model.bean.OpenEyesHomeBean
import com.fphoenixcorneae.rxretrofit.scheduler.SchedulerManager
import dagger.Module
import io.reactivex.Observable
import javax.inject.Inject

/**
 * @desc: 排行榜 Model
 */
@Module
class OpenEyesRankModel @Inject constructor() : OpenEyesBaseModel() {

    /**
     * 获取排行榜
     */
    fun requestRankList(apiUrl: String): Observable<OpenEyesHomeBean.Issue> {
        return sOpenEyesService.getIssueData(apiUrl)
            .compose(SchedulerManager.ioToMain())
    }
}
