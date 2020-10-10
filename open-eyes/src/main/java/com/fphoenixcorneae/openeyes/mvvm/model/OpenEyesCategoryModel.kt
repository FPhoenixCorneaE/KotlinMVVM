package com.fphoenixcorneae.openeyes.mvvm.model

import com.fphoenixcorneae.openeyes.mvvm.model.bean.OpenEyesCategoryBean
import com.fphoenixcorneae.rxretrofit.scheduler.SchedulerManager
import dagger.Module
import io.reactivex.Observable
import javax.inject.Inject

/**
 * @desc 分类数据 Model
 */
@Module
class OpenEyesCategoryModel @Inject constructor() : OpenEyesBaseModel() {

    /**
     * 获取分类信息
     */
    fun getCategoryData(): Observable<ArrayList<OpenEyesCategoryBean>> {
        return sOpenEyesService
            .getCategory()
            .compose(SchedulerManager.ioToMain())
    }
}