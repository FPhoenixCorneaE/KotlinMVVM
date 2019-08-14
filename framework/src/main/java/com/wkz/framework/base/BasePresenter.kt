package com.wkz.framework.base

import com.uber.autodispose.ScopeProvider
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider

/**
 * @desc: Presenter 基类
 */
open class BasePresenter<V : IView> : IPresenter<V> {

    lateinit var mView: V
        private set

    lateinit var mScopeProvider: ScopeProvider
        private set

    override fun setLifecycleScopeProvider(view: V, scopeProvider: AndroidLifecycleScopeProvider) {
        mView = view
        mScopeProvider = scopeProvider
    }

}