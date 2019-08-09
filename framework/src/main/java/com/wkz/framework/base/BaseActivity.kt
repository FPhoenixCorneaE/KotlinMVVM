package com.wkz.framework.base

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.wkz.framework.R
import com.wkz.framework.databinding.FrameworkLayoutBaseBinding
import javax.inject.Inject

/**
 * desc:BaseActivity基类
 */
abstract class BaseActivity<V : IBaseView, P : IPresenter<V, *>, DB : ViewDataBinding> : AppCompatActivity(),
    IBaseView {

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
        super.onCreate(savedInstanceState)
        mContext = this
        // 加载根布局，初始化 DataBinding
        mBaseLayoutBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext),
            R.layout.framework_layout_base, null, false
        )
        // 界面绑定
        mPresenter.attachView(this as V)
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
        // 解除绑定
        mPresenter.detachView()
        super.onDestroy()
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


