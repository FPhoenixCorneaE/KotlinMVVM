package com.wkz.framework.base

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider
import com.wkz.extension.showToast
import com.wkz.framework.R
import com.wkz.framework.databinding.FrameworkLayoutBaseBinding
import com.wkz.rxretrofit.network.exception.ErrorStatus
import com.wkz.rxretrofit.network.exception.ExceptionHandle
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject

/**
 * @desc:BaseFragment基类
 */
abstract class BaseFragment<V : IView, P : IPresenter<V>, DB : ViewDataBinding> : Fragment(),
    HasSupportFragmentInjector, IView {
    /** Kotlin中使用Dagger2 可能导致错误"Dagger does not support injection into private fields" */
    /** Kotlin 生成.java文件时属性默认为 private，给属性添加@JvmField声明可以转成 public */
    @Inject
    @JvmField
    var childFragmentInjector: DispatchingAndroidInjector<Fragment>? = null
    /** 解决RxJava内存泄漏 */
    protected val mScopeProvider: AndroidLifecycleScopeProvider by lazy {
        AndroidLifecycleScopeProvider.from(this, Lifecycle.Event.ON_DESTROY)
    }

    /** 视图是否加载完毕 */
    private var isViewPrepared = false
    /** 数据是否加载过了 */
    private var hasLoadedData = false
    /** 当前界面 Context 对象*/
    protected lateinit var mContext: Activity
    /** 当前界面 Presenter 对象 */
    @Inject
    protected lateinit var mPresenter: P
    /** 根布局 DataBinding 对象 */
    protected lateinit var mBaseLayoutBinding: FrameworkLayoutBaseBinding
    /** 当前界面布局 DataBinding 对象 */
    protected lateinit var mBinding: DB

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
        mContext = context as Activity

        // 加载根布局，初始化 DataBinding
        mBaseLayoutBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext),
            R.layout.framework_layout_base, null, false
        )
        // 设置生命周期作用域提供者
        mPresenter.setLifecycleScopeProvider(this as V, mScopeProvider)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // 加载布局，初始化 DataBinding
        mBinding = DataBindingUtil.inflate(
            inflater,
            getLayoutId(), container, false
        )

        // 将当前布局添加到根布局
        mBaseLayoutBinding.mMsvRoot.removeAllViews()
        mBaseLayoutBinding.mMsvRoot.addView(mBinding.root)
        return mBaseLayoutBinding.root
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            lazyLoadDataIfPrepared()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isViewPrepared = true
        initView()
        initListener()
        lazyLoadDataIfPrepared()
    }

    protected fun initListener() {

    }

    private fun lazyLoadDataIfPrepared() {
        if (userVisibleHint && isViewPrepared && !hasLoadedData) {
            lazyLoadData()
            hasLoadedData = true
        }
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return childFragmentInjector!!
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

    override fun showNoNetwork() {
        mBaseLayoutBinding.mMsvRoot.showNoNetwork()
    }

    override fun showError() {
        mBaseLayoutBinding.mMsvRoot.showError()
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
     * 加载布局
     */
    @LayoutRes
    abstract fun getLayoutId(): Int

    /**
     * 初始化 ViewI
     */
    abstract fun initView()

    /**
     * 懒加载数据
     */
    abstract fun lazyLoadData()
}
