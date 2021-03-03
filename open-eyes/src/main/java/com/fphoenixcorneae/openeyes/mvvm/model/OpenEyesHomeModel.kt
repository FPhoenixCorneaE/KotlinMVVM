package com.fphoenixcorneae.openeyes.mvvm.model

import com.fphoenixcorneae.openeyes.mvvm.model.bean.OpenEyesHomeBean
import com.fphoenixcorneae.rxretrofit.scheduler.SchedulerFactory
import dagger.Module
import io.reactivex.Observable
import javax.inject.Inject

/**
 * @desc 首页精选 model
 */
@Module
class OpenEyesHomeModel @Inject constructor() : OpenEyesBaseModel() {

    /**
     * 获取首页 Banner 数据
     */
    fun requestHomeData(num: Int): Observable<OpenEyesHomeBean> {
        return sOpenEyesService
            .getFirstHomeData(num)
            .compose(SchedulerFactory.ioToMain())
    }

    /**
     * 加载更多
     */
    fun loadMoreData(url: String): Observable<OpenEyesHomeBean> {
        return sOpenEyesService
            .getMoreHomeData(url)
            .compose(SchedulerFactory.ioToMain())
    }
}
