package com.fphoenixcorneae.kotlinmvvm.mvvm.presenter

import com.fphoenixcorneae.framework.base.BasePresenter
import com.fphoenixcorneae.kotlinmvvm.mvvm.contract.OpenEyesRankContract
import com.fphoenixcorneae.kotlinmvvm.mvvm.model.OpenEyesRankModel
import com.uber.autodispose.autoDisposable
import javax.inject.Inject

/**
 * @desc 排行榜 Presenter
 */
class OpenEyesRankPresenter @Inject constructor() : BasePresenter<OpenEyesRankContract.View>(),
    OpenEyesRankContract.Presenter {

    @Inject
    lateinit var rankModel: OpenEyesRankModel

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
                    // 处理异常
                    showErrorMsg(throwable)
                }
            })
    }
}