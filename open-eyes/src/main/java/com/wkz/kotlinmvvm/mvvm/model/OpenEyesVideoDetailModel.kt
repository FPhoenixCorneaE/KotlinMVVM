package com.wkz.kotlinmvvm.mvvm.model

import com.wkz.kotlinmvvm.mvvm.model.bean.OpenEyesHomeBean
import com.wkz.rxretrofit.scheduler.SchedulerManager
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