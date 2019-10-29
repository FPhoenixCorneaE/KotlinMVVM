package com.wkz.framework.base.fragment

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.wkz.extension.showToast
import com.wkz.framework.R
import com.wkz.rxretrofit.network.exception.ErrorStatus
import com.wkz.rxretrofit.network.exception.ExceptionHandle
import com.wkz.widget.MultipleStatusView

/**
 * @desc:BaseFragment基类
 */
abstract class BaseFragment : Fragment() {

    /** 当前界面 Context 对象*/
    protected lateinit var mContext: Activity
    /** 视图是否加载完毕 */
    protected var isViewPrepared = false
    /** 数据是否加载过了 */
    protected var hasLoadedData = false
    /** 根布局 */
    protected lateinit var mMsvRoot: MultipleStatusView

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context as Activity

        // 加载根布局
        mMsvRoot = LayoutInflater.from(mContext).inflate(
            R.layout.framework_layout_base, null, false
        ) as MultipleStatusView
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 加载布局
        val contentView = inflater.inflate(
            getLayoutId(), container, false
        )

        // 将当前布局添加到根布局
        mMsvRoot.removeAllViews()
        mMsvRoot.addView(contentView)
        return mMsvRoot
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

    open fun showLoading() {
        mMsvRoot.showLoading()
    }

    open fun showContent() {
        mMsvRoot.showContent()
    }

    open fun showEmpty() {
        mMsvRoot.showEmpty()
    }

    open fun showNoNetwork() {
        mMsvRoot.showNoNetwork()
    }

    open fun showError() {
        mMsvRoot.showError()
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
     * 加载布局
     */
    @LayoutRes
    abstract fun getLayoutId(): Int

    /**
     * 初始化 View
     */
    abstract fun initView()

    /**
     * 懒加载数据
     */
    abstract fun lazyLoadData()
}