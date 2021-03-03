package com.fphoenixcorneae.openeyes.mvvm.model

import com.fphoenixcorneae.openeyes.mvvm.model.bean.OpenEyesHomeBean
import com.fphoenixcorneae.rxretrofit.scheduler.SchedulerFactory
import dagger.Module
import io.reactivex.Observable
import javax.inject.Inject

/**
 * @desc 排行榜 Model
 */
@Module
class OpenEyesRankModel @Inject constructor() : OpenEyesBaseModel() {

    /**
     * 获取排行榜
     */
    fun requestRankList(apiUrl: String): Observable<OpenEyesHomeBean.Issue> {
        return sOpenEyesService.getIssueData(apiUrl)
            .compose(SchedulerFactory.ioToMain())
    }
}
