package com.fphoenixcorneae.kotlinmvvm.mvvm.presenter

import com.uber.autodispose.autoDisposable
import com.fphoenixcorneae.framework.base.BasePresenter
import com.fphoenixcorneae.kotlinmvvm.mvvm.contract.OpenEyesRankContract
import com.fphoenixcorneae.kotlinmvvm.mvvm.model.OpenEyesRankModel
import com.fphoenixcorneae.rxretrofit.network.exception.ExceptionHandle


/**
 * @desc: 排行榜 Presenter
 */
class OpenEyesRankPresenter : BasePresenter<OpenEyesRankContract.View>(), OpenEyesRankContract.Presenter {

    private val rankModel by lazy { OpenEyesRankModel() }


    /**
     *  请求排行榜数据
     */
    override fun requestRankList(apiUrl: String) {
        mView.showLoading()
        rankModel.requestRankList(apiUrl)
            .autoDisposable(mScopeProvider)
            .subscribe({ issue ->
                mView.apply {
                    showContent()
                    setRankList(issue.itemList)
                }
            }, { throwable ->
                mView.apply {
                    //处理异常
                    showError(ExceptionHandle.handleException(throwable), ExceptionHandle.errorCode)
                }
            })
    }
}