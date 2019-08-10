package com.wkz.kotlinmvvm.mvp.model

import com.wkz.kotlinmvvm.mvp.model.bean.HomeBean
import com.wkz.rxretrofit.scheduler.SchedulerManager
import io.reactivex.Observable

/**
 * @desc: 首页精选 model
 */
class HomeModel : OpenEyesBaseModel() {

    /**
     * 获取首页 Banner 数据
     */
    fun requestHomeData(num: Int): Observable<HomeBean> {
        return sOpenEyesService
            .getFirstHomeData(num)
            .compose(SchedulerManager.ioToMain())
    }

    /**
     * 加载更多
     */
    fun loadMoreData(url: String): Observable<HomeBean> {
        return sOpenEyesService
            .getMoreHomeData(url)
            .compose(SchedulerManager.ioToMain())
    }
}
