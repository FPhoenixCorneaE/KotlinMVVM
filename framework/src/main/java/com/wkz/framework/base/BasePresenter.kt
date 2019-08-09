package com.wkz.framework.base

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * @desc: Presenter 基类
 */
open class BasePresenter<V : IBaseView, M : IBaseModel> : IPresenter<V, M> {

    var mView: V? = null
        private set

    private var compositeDisposable = CompositeDisposable()

    /**
     * 界面绑定
     */
    override fun attachView(view: V) {
        this.mView = view
    }

    /**
     * 解除绑定
     */
    override fun detachView() {
        mView = null

        // 保证activity结束时取消所有正在执行的订阅
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.clear()
        }
    }

    private val isViewAttached: Boolean
        get() = mView != null

    fun checkViewAttached() {
        if (!isViewAttached) throw MvpViewNotAttachedException()
    }

    fun addSubscription(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    private class MvpViewNotAttachedException internal constructor() :
        RuntimeException("Please call IPresenter.attachView(IBaseView) before" + " requesting data to the IPresenter")
}