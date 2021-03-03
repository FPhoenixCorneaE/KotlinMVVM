package com.fphoenixcorneae.openeyes.mvvm.model

import com.fphoenixcorneae.openeyes.mvvm.model.bean.OpenEyesTabInfoBean
import com.fphoenixcorneae.rxretrofit.scheduler.SchedulerFactory
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
            .compose(SchedulerFactory.ioToMain())
    }
}
