package com.fphoenixcorneae.kotlinmvvm.mvvm.model

import com.fphoenixcorneae.kotlinmvvm.mvvm.model.bean.OpenEyesTabInfoBean
import com.fphoenixcorneae.rxretrofit.scheduler.SchedulerManager
import dagger.Module
import io.reactivex.Observable
import javax.inject.Inject

/**
 * @desc 热门 Model
 */
@Module
class OpenEyesHotModel @Inject constructor() : OpenEyesBaseModel() {

    /**
     * 获取 TabInfo
     */
    fun getTabInfo(): Observable<OpenEyesTabInfoBean> {
        return sOpenEyesService.getRankList()
            .compose(SchedulerManager.ioToMain())
    }
}
