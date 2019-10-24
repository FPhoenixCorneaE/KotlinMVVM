package com.wkz.framework.base.fragment

import android.content.Context
import androidx.fragment.app.Fragment
import com.wkz.framework.base.IPresenter
import com.wkz.framework.base.IView
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import dagger.internal.Beta
import javax.inject.Inject

/**
 * @desc:Dagger2InjectionFragment基类
 */
@Beta
abstract class Dagger2InjectionFragment<V : IView, P : IPresenter<V>> : AutoDisposeFragment(),
    HasSupportFragmentInjector, IView {
    /** Kotlin中使用Dagger2 可能导致错误"Dagger does not support injection into private fields" */
    /** Kotlin 生成.java文件时属性默认为 private，给属性添加@JvmField声明可以转成 public */
    @Inject
    @JvmField
    var childFragmentInjector: DispatchingAndroidInjector<Fragment>? = null


    /** 当前界面 Presenter 对象 */
    @Inject
    protected lateinit var mPresenter: P


    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
        // 设置生命周期作用域提供者
        mPresenter.setLifecycleScopeProvider(this as V, mScopeProvider)
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return childFragmentInjector!!
    }
}
