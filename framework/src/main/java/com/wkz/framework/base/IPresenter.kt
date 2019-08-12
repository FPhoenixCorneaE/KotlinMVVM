package com.wkz.framework.base

import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider

/**
 * @desc: Presenter 基类
 */
interface IPresenter<in V : IBaseView> {

    fun setLifecycleScopeProvider(scopeProvider: AndroidLifecycleScopeProvider)

}
