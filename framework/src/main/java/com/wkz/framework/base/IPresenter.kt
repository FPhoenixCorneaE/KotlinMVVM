package com.wkz.framework.base

import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider

/**
 * @desc: Presenter基类 接口
 */
interface IPresenter<in V : IView> {

    fun setLifecycleScopeProvider(view: V, scopeProvider: AndroidLifecycleScopeProvider)

}
