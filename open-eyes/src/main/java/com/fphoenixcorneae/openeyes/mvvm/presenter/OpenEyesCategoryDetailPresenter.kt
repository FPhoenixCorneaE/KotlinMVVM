package com.fphoenixcorneae.openeyes.mvvm.presenter

import com.uber.autodispose.autoDisposable
import com.fphoenixcorneae.framework.base.BasePresenter
import com.fphoenixcorneae.openeyes.mvvm.contract.OpenEyesCategoryDetailContract
import com.fphoenixcorneae.openeyes.mvvm.model.OpenEyesCategoryDetailModel

/**
 * @desc: 分类详情 Presenter
 */
class OpenEyesCategoryDetailPresenter : BasePresenter<OpenEyesCategoryDetailContract.View>(),
    OpenEyesCategoryDetailContract.Presenter {

    private val categoryDetailModel by lazy {
        OpenEyesCategoryDetailModel()
    }

    private var nextPageUrl: String? = null

    /**
     * 获取分类详情的列表信息
     */
    override fun getCategoryDetailList(id: Long) {
        categoryDetailModel.getCategoryDetailList(id)
            .autoDisposable(mScopeProvider)
            .subscribe({ issue ->
                mView.apply {
                    nextPageUrl = issue.nextPageUrl
                    setCateDetailList(issue.itemList)
                }
            }, { throwable ->
                mView.apply {
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
                .autoDisposable(mScopeProvider)
                .subscribe({ issue ->
                    mView.apply {
                        nextPageUrl = issue.nextPageUrl
                        setCateDetailList(issue.itemList)
                    }
                }, { throwable ->
                    mView.apply {
                        showError(throwable.toString())
                    }
                })
        }
    }
}