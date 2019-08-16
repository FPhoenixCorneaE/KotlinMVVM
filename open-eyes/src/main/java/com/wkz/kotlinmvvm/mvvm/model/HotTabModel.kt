package com.wkz.kotlinmvvm.mvvm.model

import com.wkz.kotlinmvvm.mvvm.model.bean.TabInfoBean
import com.wkz.rxretrofit.scheduler.SchedulerManager
import io.reactivex.Observable

/**
 * @desc: 热门标签 Model
 */
class HotTabModel : OpenEyesBaseModel() {

    /**
     * 获取 TabInfo
     */
    fun getTabInfo(): Observable<TabInfoBean> {

        return sOpenEyesService
            .getRankList()
            .compose(SchedulerManager.ioToMain())
    }
}
