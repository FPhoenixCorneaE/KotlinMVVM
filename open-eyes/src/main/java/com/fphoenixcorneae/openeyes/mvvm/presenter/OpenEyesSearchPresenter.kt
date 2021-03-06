package com.fphoenixcorneae.openeyes.mvvm.presenter

import com.fphoenixcorneae.framework.base.BasePresenter
import com.fphoenixcorneae.openeyes.mvvm.contract.OpenEyesSearchContract
import com.fphoenixcorneae.openeyes.mvvm.model.OpenEyesSearchModel
import com.uber.autodispose.autoDisposable
import javax.inject.Inject


/**
 * @desc 搜索 Presenter
 */
class OpenEyesSearchPresenter @Inject constructor() : BasePresenter<OpenEyesSearchContract.View>(),
    OpenEyesSearchContract.Presenter {

    private var nextPageUrl: String? = null

    @Inject
    lateinit var searchModel: OpenEyesSearchModel

    /**
     * 获取热门关键词
     */
    override fun requestHotWordData() {
        searchModel.requestHotWordData()
            .autoDisposable(mScopeProvider)
            .subscribe({ hotWords ->
                mView.apply {
                    setHotWordData(hotWords)
                }
            }, { throwable ->
                mView.apply {
                    // 处理异常
                    showErrorMsg(throwable)
                }
            })
    }

    /**
     * 根据关键词查询数据
     */
    override fun querySearchData(words: String) {
        mView.apply {
            closeSoftKeyboard()
            showLoading()
        }
        searchModel.getSearchResult(words)
            .autoDisposable(mScopeProvider)
            .subscribe({ issue ->
                mView.apply {
                    showContent()
                    if (issue.count > 0 && issue.itemList.size > 0) {
                        nextPageUrl = issue.nextPageUrl
                        setSearchResult(issue)
                    } else {
                        setEmptyView()
                    }
                }
            }, { throwable ->
                mView.apply {
                    // 处理异常
                    showErrorMsg(throwable)
                }
            })
    }

    /**
     * 加载更多数据
     */
    override fun loadMoreData() {
        nextPageUrl?.let {
            searchModel.loadMoreData(it)
                .autoDisposable(mScopeProvider)
                .subscribe({ issue ->
                    mView.apply {
                        nextPageUrl = issue.nextPageUrl
                        setSearchResult(issue)
                    }
                }, { throwable ->
                    mView.apply {
                        // 处理异常
                        showErrorMsg(throwable)
                    }
                })
        }
    }
}