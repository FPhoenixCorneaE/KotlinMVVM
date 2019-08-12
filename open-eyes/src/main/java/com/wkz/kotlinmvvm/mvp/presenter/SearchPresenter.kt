package com.wkz.kotlinmvvm.mvp.presenter

import com.wkz.framework.base.BasePresenter
import com.wkz.kotlinmvvm.mvp.contract.SearchContract
import com.wkz.kotlinmvvm.mvp.model.SearchModel
import com.wkz.rxretrofit.network.exception.ExceptionHandle


/**
 * @desc: 搜索 Presenter
 */
class SearchPresenter : BasePresenter<SearchContract.View>(), SearchContract.Presenter {

    private var nextPageUrl: String? = null

    private val searchModel by lazy { SearchModel() }


    /**
     * 获取热门关键词
     */
    override fun requestHotWordData() {
        checkViewAttached()
        mView?.apply {
            closeSoftKeyboard()
            showLoading()
        }
        addSubscription(
            disposable = searchModel.requestHotWordData()
                .subscribe({ string ->
                    mView?.apply {
                        setHotWordData(string)
                    }
                }, { throwable ->
                    mView?.apply {
                        //处理异常
                        showError(ExceptionHandle.handleException(throwable), ExceptionHandle.errorCode)
                    }
                })
        )
    }

    /**
     * 查询关键词
     */
    override fun querySearchData(words: String) {
        checkViewAttached()
        mView?.apply {
            closeSoftKeyboard()
            showLoading()
        }
        addSubscription(
            disposable = searchModel.getSearchResult(words)
                .subscribe({ issue ->
                    mView?.apply {
                        dismissLoading()
                        if (issue.count > 0 && issue.itemList.size > 0) {
                            nextPageUrl = issue.nextPageUrl
                            setSearchResult(issue)
                        } else
                            setEmptyView()
                    }
                }, { throwable ->
                    mView?.apply {
                        dismissLoading()
                        //处理异常
                        showError(ExceptionHandle.handleException(throwable), ExceptionHandle.errorCode)
                    }
                })
        )

    }

    /**
     * 加载更多数据
     */
    override fun loadMoreData() {
        checkViewAttached()
        nextPageUrl?.let {
            addSubscription(
                disposable = searchModel.loadMoreData(it)
                    .subscribe({ issue ->
                        mView?.apply {
                            nextPageUrl = issue.nextPageUrl
                            setSearchResult(issue)
                        }
                    }, { throwable ->
                        mView?.apply {
                            //处理异常
                            showError(ExceptionHandle.handleException(throwable), ExceptionHandle.errorCode)
                        }
                    })
            )
        }

    }
}