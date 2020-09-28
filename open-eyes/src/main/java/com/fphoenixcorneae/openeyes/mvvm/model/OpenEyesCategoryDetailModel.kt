package com.fphoenixcorneae.openeyes.mvvm.model

import com.fphoenixcorneae.openeyes.mvvm.model.bean.OpenEyesHomeBean
import com.fphoenixcorneae.rxretrofit.scheduler.SchedulerManager
import dagger.Module
import io.reactivex.Observable
import javax.inject.Inject

/**
 * @desc 分类详情 Model
 */
@Module
class OpenEyesCategoryDetailModel @Inject constructor() : OpenEyesBaseModel() {

    /**
     * 获取分类下的 List 数据
     */
    fun getCategoryDetailList(id: Long): Observable<OpenEyesHomeBean.Issue> {
        return sOpenEyesService
            .getCategoryDetailList(id)
            .compose(SchedulerManager.ioToMain())
    }

    /**
     * 加载更多数据
     */
    fun loadMoreData(url: String): Observable<OpenEyesHomeBean.Issue> {
        return sOpenEyesService
            .getIssueData(url)
            .compose(SchedulerManager.ioToMain())
    }
}