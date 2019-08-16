package com.wkz.kotlinmvvm.mvvm.model

import com.wkz.kotlinmvvm.mvvm.model.bean.HomeBean
import com.wkz.rxretrofit.scheduler.SchedulerManager
import io.reactivex.Observable

/**
 * @desc: 搜索 Model
 */
class SearchModel : OpenEyesBaseModel() {

    /**
     * 请求热门关键词的数据
     */
    fun requestHotWordData(): Observable<ArrayList<String>> {
        return sOpenEyesService.getHotWord()
            .compose(SchedulerManager.ioToMain())
    }


    /**
     * 搜索关键词返回的结果
     */
    fun getSearchResult(words: String): Observable<HomeBean.Issue> {
        return sOpenEyesService.getSearchData(words)
            .compose(SchedulerManager.ioToMain())
    }

    /**
     * 加载更多数据
     */
    fun loadMoreData(url: String): Observable<HomeBean.Issue> {
        return sOpenEyesService.getIssueData(url)
            .compose(SchedulerManager.ioToMain())
    }
}
