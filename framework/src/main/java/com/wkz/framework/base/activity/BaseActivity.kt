package com.wkz.framework.base.activity

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import com.wkz.extension.showToast
import com.wkz.framework.R
import com.wkz.rxretrofit.network.exception.ErrorStatus
import com.wkz.rxretrofit.network.exception.ExceptionHandle
import com.wkz.widget.MultipleStatusView
import kotlinx.android.synthetic.main.framework_layout_base.view.*

/**
 * @desc:BaseActivity基类
 */
abstract class BaseActivity : AppCompatActivity() {

    /** 当前界面 Context 对象*/
    protected lateinit var mContext: Activity
    /** 根布局 */
    protected lateinit var mBaseLayout: MultipleStatusView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = this
        // 加载根布局
        mBaseLayout = LayoutInflater.from(mContext).inflate(
            R.layout.framework_layout_base, null, false
        ) as MultipleStatusView
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

    open fun showLoading() {
        mBaseLayout.mMsvRoot.showLoading()
    }

    open fun showContent() {
        mBaseLayout.mMsvRoot.showContent()
    }

    open fun showEmpty() {
        mBaseLayout.mMsvRoot.showEmpty()
    }

    open fun showNoNetwork() {
        mBaseLayout.mMsvRoot.showNoNetwork()
    }

    open fun showError() {
        mBaseLayout.mMsvRoot.showError()
    }

    open fun showErrorMsg(t: Throwable) {
        ExceptionHandle.handleException(t)
        if (ExceptionHandle.errorCode == ErrorStatus.NETWORK_ERROR) {
            showNoNetwork()
        } else {
            showError()
        }
    }

    open fun showErrorMsg(errorMsg: CharSequence) {
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