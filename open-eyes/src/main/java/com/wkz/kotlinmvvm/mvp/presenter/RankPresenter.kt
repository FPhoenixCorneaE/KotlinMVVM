package com.wkz.kotlinmvvm.mvp.presenter

import com.wkz.framework.base.BasePresenter
import com.wkz.framework.base.IBaseModel
import com.wkz.kotlinmvvm.mvp.contract.RankContract
import com.wkz.kotlinmvvm.mvp.model.RankModel
import com.wkz.rxretrofit.network.exception.ExceptionHandle


/**
 * @desc: 排行榜 Presenter
 */
class RankPresenter : BasePresenter<RankContract.View, IBaseModel>(), RankContract.Presenter {

    private val rankModel by lazy { RankModel() }


    /**
     *  请求排行榜数据
     */
    override fun requestRankList(apiUrl: String) {
        checkViewAttached()
        mView?.showLoading()
        val disposable = rankModel.requestRankList(apiUrl)
            .subscribe({ issue ->
                mView?.apply {
                    dismissLoading()
                    setRankList(issue.itemList)
                }
            }, { throwable ->
                mView?.apply {
                    //处理异常
                    showError(ExceptionHandle.handleException(throwable), ExceptionHandle.errorCode)
                }
            })
        addSubscription(disposable)
    }
}