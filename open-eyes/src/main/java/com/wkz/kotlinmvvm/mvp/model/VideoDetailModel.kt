package com.wkz.kotlinmvvm.mvp.model

import com.wkz.kotlinmvvm.mvp.model.bean.HomeBean
import com.wkz.rxretrofit.scheduler.SchedulerManager
import io.reactivex.Observable

/**
 * @desc: 视频详情 Model
 */
class VideoDetailModel : OpenEyesBaseModel() {

    /**
     * 请求相关数据
     */
    fun requestRelatedData(id: Long): Observable<HomeBean.Issue> {
        return sOpenEyesService.getRelatedData(id)
            .compose(SchedulerManager.ioToMain())
    }
}