package com.wkz.kotlinmvvm.mvp.presenter

import com.wkz.framework.base.BasePresenter
import com.wkz.framework.base.IBaseModel
import com.wkz.kotlinmvvm.mvp.contract.HotTabContract
import com.wkz.kotlinmvvm.mvp.model.HotTabModel
import com.wkz.rxretrofit.network.exception.ExceptionHandle


/**
 * @desc: 热门标签 Presenter
 */
class HotTabPresenter : BasePresenter<HotTabContract.View, IBaseModel>(), HotTabContract.Presenter {

    private val hotTabModel by lazy { HotTabModel() }


    override fun getTabInfo() {
        checkViewAttached()
        mView?.showLoading()
        val disposable = hotTabModel.getTabInfo()
            .subscribe({ tabInfo ->
                mView?.setTabInfo(tabInfo)
            }, { throwable ->
                //处理异常
                mView?.showError(ExceptionHandle.handleException(throwable), ExceptionHandle.errorCode)
            })
        addSubscription(disposable)
    }
}