package com.fphoenixcorneae.kotlinmvvm.mvvm.model

import com.fphoenixcorneae.kotlinmvvm.mvvm.model.bean.OpenEyesTabInfoBean
import com.fphoenixcorneae.rxretrofit.scheduler.SchedulerManager
import io.reactivex.Observable

/**
 * @desc: 热门标签 Model
 */
class OpenEyesHotTabModel : OpenEyesBaseModel() {

    /**
     * 获取 TabInfo
     */
    fun getTabInfo(): Observable<OpenEyesTabInfoBean> {

        return sOpenEyesService
            .getRankList()
            .compose(SchedulerManager.ioToMain())
    }
}
