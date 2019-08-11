package com.wkz.kotlinmvvm.mvp.presenter

import com.wkz.framework.base.BasePresenter
import com.wkz.framework.base.IBaseModel
import com.wkz.kotlinmvvm.mvp.contract.FollowContract
import com.wkz.kotlinmvvm.mvp.model.FollowModel
import com.wkz.rxretrofit.network.exception.ExceptionHandle

/**
 * @desc: 关注 Presenter
 */
class FollowPresenter : BasePresenter<FollowContract.View, IBaseModel>(), FollowContract.Presenter {

    private val followModel by lazy { FollowModel() }

    private var nextPageUrl: String? = null

    /**
     *  请求关注数据
     */
    override fun requestFollowList() {
        checkViewAttached()
        mView?.showLoading()
        val disposable = followModel.requestFollowList()
            .subscribe({ issue ->
                mView?.apply {
                    dismissLoading()
                    nextPageUrl = issue.nextPageUrl
                    setFollowInfo(issue)
                }
            }, { throwable ->
                mView?.apply {
                    //处理异常
                    showError(ExceptionHandle.handleException(throwable), ExceptionHandle.errorCode)
                }
            })
        addSubscription(disposable)
    }

    /**
     * 加载更多
     */
    override fun loadMoreData() {
        val disposable = nextPageUrl?.let {
            followModel.loadMoreData(it)
                .subscribe({ issue ->
                    mView?.apply {
                        nextPageUrl = issue.nextPageUrl
                        setFollowInfo(issue)
                    }

                }, { t ->
                    mView?.apply {
                        showError(ExceptionHandle.handleException(t), ExceptionHandle.errorCode)
                    }
                })


        }
        if (disposable != null) {
            addSubscription(disposable)
        }
    }
}