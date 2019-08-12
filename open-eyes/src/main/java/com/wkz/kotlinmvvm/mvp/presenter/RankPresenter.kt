package com.wkz.kotlinmvvm.mvp.presenter

import com.uber.autodispose.autoDisposable
import com.wkz.framework.base.BasePresenter
import com.wkz.kotlinmvvm.mvp.contract.RankContract
import com.wkz.kotlinmvvm.mvp.model.RankModel
import com.wkz.rxretrofit.network.exception.ExceptionHandle


/**
 * @desc: 排行榜 Presenter
 */
class RankPresenter : BasePresenter<RankContract.View>(), RankContract.Presenter {

    private val rankModel by lazy { RankModel() }


    /**
     *  请求排行榜数据
     */
    override fun requestRankList(apiUrl: String) {
        mView?.showLoading()
        rankModel.requestRankList(apiUrl)
            .autoDisposable(mScopeProvider!!)
            .subscribe({ issue ->
                mView?.apply {
                    showContent()
                    setRankList(issue.itemList)
                }
            }, { throwable ->
                mView?.apply {
                    //处理异常
                    showError(ExceptionHandle.handleException(throwable), ExceptionHandle.errorCode)
                }
            })
    }
}