package com.wkz.kotlinmvvm.mvp.presenter

import com.wkz.framework.base.BasePresenter
import com.wkz.framework.base.IBaseModel
import com.wkz.kotlinmvvm.mvp.contract.CategoryDetailContract
import com.wkz.kotlinmvvm.mvp.model.CategoryDetailModel

/**
 * @desc: 分类详情 Presenter
 */
class CategoryDetailPresenter : BasePresenter<CategoryDetailContract.View, IBaseModel>(),
    CategoryDetailContract.Presenter {

    private val categoryDetailModel by lazy {
        CategoryDetailModel()
    }

    private var nextPageUrl: String? = null

    /**
     * 获取分类详情的列表信息
     */
    override fun getCategoryDetailList(id: Long) {
        checkViewAttached()
        val disposable = categoryDetailModel.getCategoryDetailList(id)
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

        addSubscription(disposable)
    }

    /**
     * 加载更多数据
     */
    override fun loadMoreData() {
        val disposable = nextPageUrl?.let {
            categoryDetailModel.loadMoreData(it)
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

        disposable?.let { addSubscription(it) }
    }
}