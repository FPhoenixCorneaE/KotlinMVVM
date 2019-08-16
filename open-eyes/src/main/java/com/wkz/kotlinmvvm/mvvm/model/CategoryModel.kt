package com.wkz.kotlinmvvm.mvvm.model

import com.wkz.kotlinmvvm.mvvm.model.bean.CategoryBean
import com.wkz.rxretrofit.scheduler.SchedulerManager
import io.reactivex.Observable

/**
 * @desc: 分类数据 Model
 */
class CategoryModel : OpenEyesBaseModel() {

    /**
     * 获取分类信息
     */
    fun getCategoryData(): Observable<ArrayList<CategoryBean>> {
        return sOpenEyesService
            .getCategory()
            .compose(SchedulerManager.ioToMain())
    }
}