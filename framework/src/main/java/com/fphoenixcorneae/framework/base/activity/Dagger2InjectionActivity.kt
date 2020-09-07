package com.fphoenixcorneae.framework.base.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.fphoenixcorneae.framework.base.IPresenter
import com.fphoenixcorneae.framework.base.IView
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasFragmentInjector
import dagger.android.support.HasSupportFragmentInjector
import dagger.internal.Beta
import javax.inject.Inject

/**
 * @desc:Dagger2InjectionActivity基类
 */
@Beta
abstract class Dagger2InjectionActivity<V : IView, P : IPresenter<V>> : AutoDisposeActivity(),
    HasFragmentInjector, HasSupportFragmentInjector, IView {

    /** Kotlin中使用Dagger2 可能导致错误"Dagger does not support injection into private fields" */
    /** Kotlin 生成.java文件时属性默认为 private，给属性添加@JvmField声明可以转成 public */
    @Inject
    @JvmField
    var supportFragmentInjector: DispatchingAndroidInjector<Fragment>? = null
    @Inject
    @JvmField
    var frameworkFragmentInjector: DispatchingAndroidInjector<android.app.Fragment>? = null

    /** 当前界面 Presenter 对象 */
    @Inject
    protected lateinit var mPresenter: P

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        // 设置生命周期作用域提供者
        mPresenter.setLifecycleScopeProvider(this as V, mScopeProvider)
        super.onCreate(savedInstanceState)
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return supportFragmentInjector!!
    }

    override fun fragmentInjector(): AndroidInjector<android.app.Fragment> {
        return frameworkFragmentInjector!!
    }
}


