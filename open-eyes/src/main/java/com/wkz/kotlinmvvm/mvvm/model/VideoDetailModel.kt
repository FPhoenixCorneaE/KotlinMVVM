package com.wkz.kotlinmvvm.mvvm.model

import com.wkz.kotlinmvvm.mvvm.model.bean.HomeBean
import com.wkz.rxretrofit.scheduler.SchedulerManager
import dagger.Module
import io.reactivex.Observable
import javax.inject.Inject

/**
 * @desc: 视频详情 Model
 */
@Module
class VideoDetailModel @Inject constructor() : OpenEyesBaseModel() {

    /**
     * 请求相关数据
     */
    fun requestRelatedData(id: Long): Observable<HomeBean.Issue> {
        return sOpenEyesService.getRelatedData(id)
            .compose(SchedulerManager.ioToMain())
    }
}