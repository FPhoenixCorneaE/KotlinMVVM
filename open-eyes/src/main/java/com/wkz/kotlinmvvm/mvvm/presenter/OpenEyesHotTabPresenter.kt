package com.wkz.kotlinmvvm.mvvm.presenter

import com.uber.autodispose.autoDisposable
import com.wkz.framework.base.BasePresenter
import com.wkz.kotlinmvvm.mvvm.contract.OpenEyesHotTabContract
import com.wkz.kotlinmvvm.mvvm.model.OpenEyesHotTabModel
import com.wkz.rxretrofit.network.exception.ExceptionHandle


/**
 * @desc: 热门标签 Presenter
 */
class OpenEyesHotTabPresenter : BasePresenter<OpenEyesHotTabContract.View>(), OpenEyesHotTabContract.Presenter {

    private val hotTabModel by lazy { OpenEyesHotTabModel() }


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