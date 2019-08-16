package com.wkz.kotlinmvvm.mvvm.presenter

import com.uber.autodispose.autoDisposable
import com.wkz.framework.base.BasePresenter
import com.wkz.kotlinmvvm.mvvm.contract.FollowContract
import com.wkz.kotlinmvvm.mvvm.model.FollowModel
import com.wkz.rxretrofit.network.exception.ExceptionHandle

/**
 * @desc: 关注 Presenter
 */
class FollowPresenter : BasePresenter<FollowContract.View>(), FollowContract.Presenter {

    private val followModel by lazy { FollowModel() }

    private var nextPageUrl: String? = null

    /**
     *  请求关注数据
     */
    override fun requestFollowList() {
        mView?.showLoading()
        followModel.requestFollowList()
            .autoDisposable(mScopeProvider!!)
            .subscribe({ issue ->
                mView?.apply {
                    showContent()
                    nextPageUrl = issue.nextPageUrl
                    setFollowInfo(issue)
                }
            }, { throwable ->
                mView?.apply {
                    //处理异常
                    showError(ExceptionHandle.handleException(throwable), ExceptionHandle.errorCode)
                }
            })
    }

    /**
     * 加载更多
     */
    override fun loadMoreData() {
        nextPageUrl?.let {
            followModel.loadMoreData(it)
                .autoDisposable(mScopeProvider!!)
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
    }
}