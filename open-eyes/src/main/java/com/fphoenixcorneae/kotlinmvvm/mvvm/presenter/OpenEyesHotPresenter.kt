package com.fphoenixcorneae.kotlinmvvm.mvvm.presenter

import com.fphoenixcorneae.framework.base.BasePresenter
import com.fphoenixcorneae.kotlinmvvm.mvvm.contract.OpenEyesHotTabContract
import com.fphoenixcorneae.kotlinmvvm.mvvm.model.OpenEyesHotModel
import com.uber.autodispose.autoDisposable
import javax.inject.Inject


/**
 * @desc 热门 Presenter
 */
class OpenEyesHotPresenter @Inject constructor() : BasePresenter<OpenEyesHotTabContract.View>(),
    OpenEyesHotTabContract.Presenter {

    @Inject
    lateinit var hotModel: OpenEyesHotModel

    override fun getTabInfo() {
        mView.showLoading()
        hotModel.getTabInfo()
            .autoDisposable(mScopeProvider)
            .subscribe({ tabInfo ->
                mView.apply {
                    showContent()
                    setTabInfo(tabInfo)
                }
            }, { throwable ->
                // 处理异常
                mView.apply {
                    showErrorMsg(throwable)
                }
            })
    }
}