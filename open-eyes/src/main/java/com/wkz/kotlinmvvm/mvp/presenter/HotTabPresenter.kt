package com.wkz.kotlinmvvm.mvp.presenter

import com.uber.autodispose.autoDisposable
import com.wkz.framework.base.BasePresenter
import com.wkz.kotlinmvvm.mvp.contract.HotTabContract
import com.wkz.kotlinmvvm.mvp.model.HotTabModel
import com.wkz.rxretrofit.network.exception.ExceptionHandle


/**
 * @desc: 热门标签 Presenter
 */
class HotTabPresenter : BasePresenter<HotTabContract.View>(), HotTabContract.Presenter {

    private val hotTabModel by lazy { HotTabModel() }


    override fun getTabInfo() {
        mView?.showLoading()
        hotTabModel.getTabInfo()
            .autoDisposable(mScopeProvider!!)
            .subscribe({ tabInfo ->
                mView?.setTabInfo(tabInfo)
            }, { throwable ->
                //处理异常
                mView?.showError(ExceptionHandle.handleException(throwable), ExceptionHandle.errorCode)
            })
    }
}