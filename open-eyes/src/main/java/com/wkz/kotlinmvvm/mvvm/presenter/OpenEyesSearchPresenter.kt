package com.wkz.kotlinmvvm.mvvm.presenter

import com.uber.autodispose.autoDisposable
import com.wkz.framework.base.BasePresenter
import com.wkz.kotlinmvvm.mvvm.contract.OpenEyesSearchContract
import com.wkz.kotlinmvvm.mvvm.model.OpenEyesSearchModel
import com.wkz.rxretrofit.network.exception.ExceptionHandle


/**
 * @desc: 搜索 Presenter
 */
class OpenEyesSearchPresenter : BasePresenter<OpenEyesSearchContract.View>(), OpenEyesSearchContract.Presenter {

    private var nextPageUrl: String? = null

    private val searchModel by lazy { OpenEyesSearchModel() }


    /**
     * 获取热门关键词
     */
    override fun requestHotWordData() {
        mView?.apply {
            closeSoftKeyboard()
            showLoading()
        }
        searchModel.requestHotWordData()
            .autoDisposable(mScopeProvider!!)
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
    }

    /**
     * 查询关键词
     */
    override fun querySearchData(words: String) {
        mView?.apply {
            closeSoftKeyboard()
            showLoading()
        }
        searchModel.getSearchResult(words)
            .autoDisposable(mScopeProvider!!)
            .subscribe({ issue ->
                mView?.apply {
                    showContent()
                    if (issue.count > 0 && issue.itemList.size > 0) {
                        nextPageUrl = issue.nextPageUrl
                        setSearchResult(issue)
                    } else
                        setEmptyView()
                }
            }, { throwable ->
                mView?.apply {
                    showContent()
                    //处理异常
                    showError(ExceptionHandle.handleException(throwable), ExceptionHandle.errorCode)
                }
            })
    }

    /**
     * 加载更多数据
     */
    override fun loadMoreData() {
        nextPageUrl?.let {
            searchModel.loadMoreData(it)
                .autoDisposable(mScopeProvider!!)
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
        }
    }
}