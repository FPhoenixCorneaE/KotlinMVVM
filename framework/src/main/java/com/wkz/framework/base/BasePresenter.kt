package com.wkz.framework.base

import com.uber.autodispose.ScopeProvider
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider

/**
 * @desc: Presenter 基类
 */
open class BasePresenter<V : IView> : IPresenter<V> {

    var mView: V? = null
        private set

    var mScopeProvider: ScopeProvider? = null
        protected set

    override fun setLifecycleScopeProvider(scopeProvider: AndroidLifecycleScopeProvider) {
        mScopeProvider = scopeProvider
    }

}