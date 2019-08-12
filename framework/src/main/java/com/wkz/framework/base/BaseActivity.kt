package com.wkz.framework.base

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.uber.autodispose.AutoDispose
import com.uber.autodispose.AutoDisposeConverter
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider
import com.wkz.extension.showToast
import com.wkz.framework.R
import com.wkz.framework.databinding.FrameworkLayoutBaseBinding
import com.wkz.rxretrofit.network.exception.ExceptionHandle
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasFragmentInjector
import dagger.android.support.HasSupportFragmentInjector
import dagger.internal.Beta
import javax.inject.Inject

/**
 * @desc:BaseActivity基类
 */
@Beta
abstract class BaseActivity<V : IBaseView, P : IPresenter<V>, DB : ViewDataBinding> : AppCompatActivity(),
    HasFragmentInjector, HasSupportFragmentInjector, IBaseView {

    /** Kotlin中使用Dagger2 可能导致错误"Dagger does not support injection into private fields" */
    /** Kotlin 生成.java文件时属性默认为 private，给属性添加@JvmField声明可以转成 public */
    @Inject
    @JvmField
    var supportFragmentInjector: DispatchingAndroidInjector<Fragment>? = null
    @Inject
    @JvmField
    var frameworkFragmentInjector: DispatchingAndroidInjector<android.app.Fragment>? = null

    /** 解决RxJava内存泄漏 */
    protected val mScopeProvider: AndroidLifecycleScopeProvider by lazy {
        AndroidLifecycleScopeProvider.from(this, Lifecycle.Event.ON_DESTROY)
    }

    /** 当前界面 Context 对象*/
    protected lateinit var mContext: Activity
    /** 当前界面 Presenter 对象 */
    @Inject
    protected lateinit var mPresenter: P
    /** 根布局 DataBinding 对象 */
    protected lateinit var mBaseLayoutBinding: FrameworkLayoutBaseBinding
    /** 当前界面布局 DataBinding 对象 */
    protected lateinit var mBinding: DB

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        mContext = this
        // 加载根布局，初始化 DataBinding
        mBaseLayoutBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext),
            R.layout.framework_layout_base, null, false
        )
        // 设置生命周期作用域提供者
        mPresenter.setLifecycleScopeProvider(mScopeProvider)
        setContentView(getLayoutId())
        initView()
        initListener()
        initData(savedInstanceState)
    }

    override fun setContentView(layoutResID: Int) {
        // 加载布局，初始化 DataBinding
        mBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext),
            layoutResID, null, false
        )

        // 将当前布局添加到根布局
        mBaseLayoutBinding.mMsvRoot.removeAllViews()
        mBaseLayoutBinding.mMsvRoot.addView(mBinding.root)

        super.setContentView(mBaseLayoutBinding.root)
    }

    protected fun initListener() {

    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return supportFragmentInjector!!
    }

    override fun fragmentInjector(): AndroidInjector<android.app.Fragment> {
        return frameworkFragmentInjector!!
    }

    protected fun <T> bindLifecycle(): AutoDisposeConverter<T> {
        return AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this))
    }

    override fun showLoading() {
        mBaseLayoutBinding.mMsvRoot.showLoading()
    }

    override fun showContent() {
        mBaseLayoutBinding.mMsvRoot.showContent()
    }

    override fun showEmpty() {
        mBaseLayoutBinding.mMsvRoot.showEmpty()
    }

    override fun showError() {
        mBaseLayoutBinding.mMsvRoot.showError()
    }

    override fun showErrorMsg(t: Throwable) {
        ExceptionHandle.handleException(t)
    }

    override fun showErrorMsg(errorMsg: CharSequence) {
        showToast(errorMsg)
    }

    /**
     *  加载布局
     */
    @LayoutRes
    abstract fun getLayoutId(): Int

    /**
     * 初始化 View
     */
    abstract fun initView()

    /**
     * 初始化数据
     */
    abstract fun initData(savedInstanceState: Bundle?)
}


