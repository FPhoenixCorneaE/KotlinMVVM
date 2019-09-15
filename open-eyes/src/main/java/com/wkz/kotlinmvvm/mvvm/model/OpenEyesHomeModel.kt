package com.wkz.kotlinmvvm.mvvm.model

import com.wkz.kotlinmvvm.mvvm.model.bean.OpenEyesHomeBean
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
    fun requestHomeData(num: Int): Observable<OpenEyesHomeBean> {
        return sOpenEyesService
            .getFirstHomeData(num)
            .compose(SchedulerManager.ioToMain())
    }

    /**
     * 加载更多
     */
    fun loadMoreData(url: String): Observable<OpenEyesHomeBean> {
        return sOpenEyesService
            .getMoreHomeData(url)
            .compose(SchedulerManager.ioToMain())
    }
}
