package com.fphoenixcorneae.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.isVisible
import com.fphoenixcorneae.widget.multiplestatus.R
import java.util.*

/**
 * @desc：一个方便在多种状态切换的 View
 *        注意：多状态布局不能继承 RelativeLayout 或者 LinearLayout,否则 AgentWeb 加载网页空白
 * @date: 2016-01-15 10:20.
 */
class MultipleStatusView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var mEmptyView: View? = null
    private var mErrorView: View? = null
    private var mLoadingView: View? = null
    private var mNoNetworkView: View? = null
    private var mContentView: View? = null
    private val mEmptyViewResId: Int
    private val mErrorViewResId: Int
    private val mLoadingViewResId: Int
    private val mNoNetworkViewResId: Int
    private val mContentViewResId: Int

    /**
     * 获取当前状态
     */
    private var mCurrentViewStatus: Int = 0
    private var mInflater: LayoutInflater? = null
    private var mOnRetryClickListener: OnClickListener? = null

    private val mOtherIds = ArrayList<Int>()

    init {
        val a =
            context.obtainStyledAttributes(attrs, R.styleable.MultipleStatusView, defStyleAttr, 0)
        mEmptyViewResId = a.getResourceId(
            R.styleable.MultipleStatusView_msv_emptyView,
            R.layout.multiple_status_layout_empty
        )
        mErrorViewResId = a.getResourceId(
            R.styleable.MultipleStatusView_msv_errorView,
            R.layout.multiple_status_layout_error
        )
        mLoadingViewResId = a.getResourceId(
            R.styleable.MultipleStatusView_msv_loadingView,
            R.layout.multiple_status_layout_loading
        )
        mNoNetworkViewResId = a.getResourceId(
            R.styleable.MultipleStatusView_msv_noNetworkView,
            R.layout.multiple_status_layout_no_network
        )
        mContentViewResId = a.getResourceId(
            R.styleable.MultipleStatusView_msv_contentView,
            NO_ID
        )
        a.recycle()
        mInflater = LayoutInflater.from(getContext())
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        showContent()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        remove(mEmptyView, mLoadingView, mErrorView, mNoNetworkView)
        mOtherIds.clear()
        mOnRetryClickListener = null
        mInflater = null
    }

    /**
     * 设置重试点击事件
     *
     * @param onRetryClickListener 重试点击事件
     */
    fun setOnRetryClickListener(onRetryClickListener: OnClickListener) {
        this.mOnRetryClickListener = onRetryClickListener
    }

    /**
     * 显示空视图
     * @param layoutId 自定义布局文件
     * @param layoutParams 布局参数
     */
    @JvmOverloads
    fun showEmpty(
        layoutId: Int = mEmptyViewResId,
        layoutParams: ViewGroup.LayoutParams = DEFAULT_LAYOUT_PARAMS
    ) {
        showEmpty(inflateView(layoutId), layoutParams)
    }

    /**
     * 显示空视图
     * @param view 自定义视图
     * @param layoutParams 布局参数
     */
    fun showEmpty(view: View?, layoutParams: ViewGroup.LayoutParams) {
        checkNull(view, "Empty view is null!")
        mCurrentViewStatus = STATUS_EMPTY
        if (null == mEmptyView) {
            mEmptyView = view
            val emptyRetryView = mEmptyView!!.findViewById<View>(R.id.empty_retry_view)
            if (null != mOnRetryClickListener && null != emptyRetryView) {
                emptyRetryView.setOnClickListener(mOnRetryClickListener)
            }
            mOtherIds.add(mEmptyView!!.id)
            addView(mEmptyView, 0, layoutParams)
        }
        showViewById(mEmptyView!!.id)
    }

    /**
     * 显示错误视图
     * @param layoutId 自定义布局文件
     * @param layoutParams 布局参数
     */
    @JvmOverloads
    fun showError(
        layoutId: Int = mErrorViewResId,
        layoutParams: ViewGroup.LayoutParams = DEFAULT_LAYOUT_PARAMS
    ) {
        showError(inflateView(layoutId), layoutParams)
    }

    /**
     * 显示错误视图
     * @param view 自定义视图
     * @param layoutParams 布局参数
     */
    fun showError(
        view: View?,
        layoutParams: ViewGroup.LayoutParams = DEFAULT_LAYOUT_PARAMS
    ) {
        checkNull(view, "Error view is null!")
        mCurrentViewStatus = STATUS_ERROR
        if (null == mErrorView) {
            mErrorView = view
            val errorRetryView = mErrorView!!.findViewById<View>(R.id.error_retry_view)
            if (null != mOnRetryClickListener && null != errorRetryView) {
                errorRetryView.setOnClickListener(mOnRetryClickListener)
            }
            mOtherIds.add(mErrorView!!.id)
            addView(mErrorView, 0, layoutParams)
        }
        showViewById(mErrorView!!.id)
    }

    /**
     * 显示加载中视图
     * @param layoutId 自定义布局文件
     * @param layoutParams 布局参数
     */
    @JvmOverloads
    fun showLoading(
        layoutId: Int = mLoadingViewResId,
        layoutParams: ViewGroup.LayoutParams = DEFAULT_LAYOUT_PARAMS
    ) {
        showLoading(inflateView(layoutId), layoutParams)
    }

    /**
     * 显示加载中视图
     * @param view 自定义视图
     * @param layoutParams 布局参数
     */
    fun showLoading(
        view: View?,
        layoutParams: ViewGroup.LayoutParams = DEFAULT_LAYOUT_PARAMS
    ) {
        checkNull(view, "Loading view is null!")
        mCurrentViewStatus = STATUS_LOADING
        if (null == mLoadingView) {
            mLoadingView = view
            mOtherIds.add(mLoadingView!!.id)
            addView(mLoadingView, 0, layoutParams)
        }
        showViewById(mLoadingView!!.id)
    }

    /**
     * 显示无网络视图
     * @param layoutId 自定义布局文件
     * @param layoutParams 布局参数
     */
    @JvmOverloads
    fun showNoNetwork(
        layoutId: Int = mNoNetworkViewResId,
        layoutParams: ViewGroup.LayoutParams = DEFAULT_LAYOUT_PARAMS
    ) {
        showNoNetwork(inflateView(layoutId), layoutParams)
    }

    /**
     * 显示无网络视图
     * @param view 自定义视图
     * @param layoutParams 布局参数
     */
    fun showNoNetwork(
        view: View?,
        layoutParams: ViewGroup.LayoutParams = DEFAULT_LAYOUT_PARAMS
    ) {
        checkNull(view, "No network view is null!")
        mCurrentViewStatus = STATUS_NO_NETWORK
        if (null == mNoNetworkView) {
            mNoNetworkView = view
            val noNetworkRetryView = mNoNetworkView!!.findViewById<View>(R.id.no_network_retry_view)
            if (null != mOnRetryClickListener && null != noNetworkRetryView) {
                noNetworkRetryView.setOnClickListener(mOnRetryClickListener)
            }
            mOtherIds.add(mNoNetworkView!!.id)
            addView(mNoNetworkView, 0, layoutParams)
        }
        showViewById(mNoNetworkView!!.id)
    }

    /**
     * 显示内容视图
     */
    fun showContent() {
        mCurrentViewStatus = STATUS_CONTENT
        if (null == mContentView && mContentViewResId != NO_ID) {
            mContentView = mInflater?.inflate(mContentViewResId, null)
            mContentView?.let {
                addView(it, 0, DEFAULT_LAYOUT_PARAMS)
            }
        }
        showContentView()
    }

    private fun showContentView() {
        val childCount = childCount
        for (i in 0 until childCount) {
            val view = getChildAt(i)
            view?.let {
                it.isVisible = !mOtherIds.contains(it.id)
            }
        }
    }

    private fun inflateView(layoutId: Int): View? {
        return mInflater?.inflate(layoutId, null)
    }

    /**
     * 根据 View 的 id 显示 View
     */
    private fun showViewById(viewId: Int) {
        val childCount = childCount
        for (i in 0 until childCount) {
            val view = getChildAt(i)
            view?.isVisible = view?.id == viewId
        }
    }

    /**
     * 检查是否为空
     */
    private fun checkNull(`object`: Any?, hint: String) {
        if (null == `object`) {
            throw NullPointerException(hint)
        }
    }

    /**
     * 移除 View
     */
    private fun remove(vararg views: View?) {
        views.forEach { view ->
            view?.let {
                removeView(it)
            }
        }
    }

    companion object {

        val DEFAULT_LAYOUT_PARAMS = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT
        )

        /**
         * 显示数据内容
         */
        const val STATUS_CONTENT = 0

        /**
         * 显示加载中视图
         */
        const val STATUS_LOADING = 1

        /**
         * 显示空视图
         */
        const val STATUS_EMPTY = 2

        /**
         * 显示错误视图
         */
        const val STATUS_ERROR = 3

        /**
         * 显示无网络视图
         */
        const val STATUS_NO_NETWORK = 4
    }
}




