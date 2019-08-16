package com.wkz.kotlinmvvm.mvvm.model

import com.wkz.kotlinmvvm.mvvm.model.bean.HomeBean
import com.wkz.rxretrofit.scheduler.SchedulerManager
import dagger.Module
import io.reactivex.Observable
import javax.inject.Inject

/**
 * @desc: 首页精选 model
 */
@Module
class OpenEyesHomeModel @Inject constructor() : OpenEyesBaseModel() {

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
