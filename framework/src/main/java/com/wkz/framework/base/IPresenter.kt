package com.wkz.framework.base

/**
 * @desc: Presenter 基类
 */
interface IPresenter<in V : IBaseView> {

    fun attachView(view: V)

    fun detachView()

}
