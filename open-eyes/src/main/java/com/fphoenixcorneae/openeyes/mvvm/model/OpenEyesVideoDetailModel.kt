package com.fphoenixcorneae.openeyes.mvvm.model

import com.fphoenixcorneae.openeyes.mvvm.model.bean.OpenEyesHomeBean
import com.fphoenixcorneae.rxretrofit.scheduler.SchedulerManager
import dagger.Module
import io.reactivex.Observable
import javax.inject.Inject

/**
 * @desc: 视频详情 Model
 */
@Module
class OpenEyesVideoDetailModel @Inject constructor() : OpenEyesBaseModel() {

    /**
     * 请求相关数据
     */
    fun requestRelatedData(id: Long): Observable<OpenEyesHomeBean.Issue> {
        return sOpenEyesService.getRelatedData(id)
            .compose(SchedulerManager.ioToMain())
    }
}