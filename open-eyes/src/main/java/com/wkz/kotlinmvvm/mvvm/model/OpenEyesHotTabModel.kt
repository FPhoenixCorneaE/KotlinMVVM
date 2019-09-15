package com.wkz.kotlinmvvm.mvvm.model

import com.wkz.kotlinmvvm.mvvm.model.bean.OpenEyesTabInfoBean
import com.wkz.rxretrofit.scheduler.SchedulerManager
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
