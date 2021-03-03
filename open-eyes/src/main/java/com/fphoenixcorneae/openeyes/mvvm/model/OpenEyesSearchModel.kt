package com.fphoenixcorneae.openeyes.mvvm.model

import com.fphoenixcorneae.openeyes.mvvm.model.bean.OpenEyesHomeBean
import com.fphoenixcorneae.rxretrofit.scheduler.SchedulerFactory
import dagger.Module
import io.reactivex.Observable
import javax.inject.Inject

/**
 * @desc 搜索 Model
 */
@Module
class OpenEyesSearchModel @Inject constructor() : OpenEyesBaseModel() {

    /**
     * 请求热门关键词的数据
     */
    fun requestHotWordData(): Observable<ArrayList<String>> {
        return sOpenEyesService.getHotWord()
            .compose(SchedulerFactory.ioToMain())
    }


    /**
     * 搜索关键词返回的结果
     */
    fun getSearchResult(words: String): Observable<OpenEyesHomeBean.Issue> {
        return sOpenEyesService.getSearchData(words)
            .compose(SchedulerFactory.ioToMain())
    }

    /**
     * 加载更多数据
     */
    fun loadMoreData(url: String): Observable<OpenEyesHomeBean.Issue> {
        return sOpenEyesService.getIssueData(url)
            .compose(SchedulerFactory.ioToMain())
    }
}
