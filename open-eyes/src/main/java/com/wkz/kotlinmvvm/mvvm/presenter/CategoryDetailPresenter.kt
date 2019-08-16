package com.wkz.kotlinmvvm.mvvm.presenter

import com.uber.autodispose.autoDisposable
import com.wkz.framework.base.BasePresenter
import com.wkz.kotlinmvvm.mvvm.contract.CategoryDetailContract
import com.wkz.kotlinmvvm.mvvm.model.CategoryDetailModel

/**
 * @desc: 分类详情 Presenter
 */
class CategoryDetailPresenter : BasePresenter<CategoryDetailContract.View>(),
    CategoryDetailContract.Presenter {

    private val categoryDetailModel by lazy {
        CategoryDetailModel()
    }

    private var nextPageUrl: String? = null

    /**
     * 获取分类详情的列表信息
     */
    override fun getCategoryDetailList(id: Long) {
        categoryDetailModel.getCategoryDetailList(id)
            .autoDisposable(mScopeProvider!!)
            .subscribe({ issue ->
                mView?.apply {
                    nextPageUrl = issue.nextPageUrl
                    setCateDetailList(issue.itemList)
                }
            }, { throwable ->
                mView?.apply {
                    showError(throwable.toString())
                }
            })
    }

    /**
     * 加载更多数据
     */
    override fun loadMoreData() {
        nextPageUrl?.let {
            categoryDetailModel.loadMoreData(it)
                .autoDisposable(mScopeProvider!!)
                .subscribe({ issue ->
                    mView?.apply {
                        nextPageUrl = issue.nextPageUrl
                        setCateDetailList(issue.itemList)
                    }
                }, { throwable ->
                    mView?.apply {
                        showError(throwable.toString())
                    }
                })
        }
    }
}