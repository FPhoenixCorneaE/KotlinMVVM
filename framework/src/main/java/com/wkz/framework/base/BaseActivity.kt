package com.wkz.framework.base

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider
import com.wkz.extension.showToast
import com.wkz.framework.R
import com.wkz.rxretrofit.network.exception.ErrorStatus
import com.wkz.rxretrofit.network.exception.ExceptionHandle
import com.wkz.widget.MultipleStatusView
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasFragmentInjector
import dagger.android.support.HasSupportFragmentInjector
import dagger.internal.Beta
import kotlinx.android.synthetic.main.framework_layout_base.view.*
import javax.inject.Inject

/**
 * @desc:BaseActivity基类
 */
@Beta
abstract class BaseActivity<V : IView, P : IPresenter<V>> : AppCompatActivity(),
    HasFragmentInjector, HasSupportFragmentInjector, IView {

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
    /** 根布局 */
    protected lateinit var mBaseLayout: MultipleStatusView


    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        mContext = this
        // 加载根布局
        mBaseLayout = LayoutInflater.from(mContext).inflate(
            R.layout.framework_layout_base, null, false
        ) as MultipleStatusView
        // 设置生命周期作用域提供者
        mPresenter.setLifecycleScopeProvider(this as V, mScopeProvider)
        setContentView(getLayoutId())
        initView()
        initListener()
        initData(savedInstanceState)
    }

    override fun setContentView(layoutResID: Int) {
        // 加载布局
        val contentView =LayoutInflater.from(mContext).inflate(
            getLayoutId(), null, false
        )

        // 将当前布局添加到根布局
        mBaseLayout.mMsvRoot.removeAllViews()
        mBaseLayout.mMsvRoot.addView(contentView)

        super.setContentView(mBaseLayout)
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

    override fun showLoading() {
        mBaseLayout.mMsvRoot.showLoading()
    }

    override fun showContent() {
        mBaseLayout.mMsvRoot.showContent()
    }

    override fun showEmpty() {
        mBaseLayout.mMsvRoot.showEmpty()
    }

    override fun showNoNetwork() {
        mBaseLayout.mMsvRoot.showNoNetwork()
    }

    override fun showError() {
        mBaseLayout.mMsvRoot.showError()
    }

    override fun showErrorMsg(t: Throwable) {
        ExceptionHandle.handleException(t)
        if (ExceptionHandle.errorCode == ErrorStatus.NETWORK_ERROR) {
            showNoNetwork()
        } else {
            showError()
        }
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


