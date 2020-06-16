package com.wkz.wanandroid.mvvm.view.fragment

import androidx.lifecycle.Observer
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import com.wkz.adapter.AnimationType
import com.wkz.extension.isNonNullAndNotEmpty
import com.wkz.extension.viewModel
import com.wkz.wanandroid.R
import com.wkz.wanandroid.mvvm.view.adapter.WanAndroidMineIntegralRecordAdapter
import com.wkz.wanandroid.mvvm.viewmodel.WanAndroidMineIntegralViewModel
import kotlinx.android.synthetic.main.wan_android_fragment_integral_record.*

/**
 * @desc: 积分记录Fragment
 * @date: 2020-06-10 17:17
 */
class WanAndroidIntegralRecordFragment : WanAndroidBaseFragment(), OnRefreshLoadMoreListener {

    private val mIntegralRecordAdapter by lazy(LazyThreadSafetyMode.NONE) {
        WanAndroidMineIntegralRecordAdapter()
    }

    /* 积分ViewModel */
    private val mIntegralViewModel by viewModel<WanAndroidMineIntegralViewModel>()

    override fun getLayoutId(): Int = R.layout.wan_android_fragment_integral_record

    override fun initView() {
        // 设置标题栏主题样式
        setCommonTitleBarTheme(mTbTitleBar)
        initIntegralRankingRecyclerView()
    }

    private fun initIntegralRankingRecyclerView() {
        mIntegralRecordAdapter.showItemAnim(AnimationType.TRANSLATE_FROM_BOTTOM, false)
        mRvIntegralRecord.apply {
            adapter = mIntegralRecordAdapter
        }
    }

    override fun initListener() {
        mSrlRefresh.setOnRefreshLoadMoreListener(this)
        mIntegralViewModel.apply {
            mIntegralRecordUIState.mRefreshSuccess.observe(viewLifecycleOwner, Observer {
                when {
                    it -> mSrlRefresh.finishRefresh()
                    else -> showError()
                }
            })
            mIntegralRecordUIState.mLoadMoreSuccess.observe(viewLifecycleOwner, Observer {
                mSrlRefresh.finishLoadMore(it)
            })
            mIntegralRecordUIState.mLoadMoreNoData.observe(viewLifecycleOwner, Observer {
                mSrlRefresh.finishLoadMoreWithNoMoreData()
            })
            mIntegralRecord.observe(viewLifecycleOwner, Observer {
                it?.apply {
                    when {
                        isRefresh() -> {
                            mIntegralRecordAdapter.dataList.clear()
                        }
                    }
                    mIntegralRecordAdapter.dataList.addAll(it.datas)
                    mIntegralRecordAdapter.notifyDataSetChanged()
                }
            })
        }
    }

    override fun lazyLoadData() {
        mSrlRefresh.autoRefresh()
    }

    override fun isAlreadyLoadedData(): Boolean =
        mIntegralRecordAdapter.dataList.isNonNullAndNotEmpty()

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        mIntegralViewModel.loadMoreIntegralRecord()
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        mIntegralViewModel.refreshIntegralRecord()
    }
}