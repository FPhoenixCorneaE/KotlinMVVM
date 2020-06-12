package com.wkz.wanandroid.mvvm.view.fragment

import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener
import com.wkz.adapter.AnimationType
import com.wkz.extension.isNonNullAndNotEmpty
import com.wkz.extension.viewModel
import com.wkz.wanandroid.R
import com.wkz.wanandroid.mvvm.view.adapter.WanAndroidMineIntegralRankingAdapter
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

    private val mIntegralRankingViewModel by viewModel<WanAndroidMineIntegralViewModel>()

    override fun getLayoutId(): Int = R.layout.wan_android_fragment_integral_record

    override fun initView() {
        // 设置标题栏主题样式
        setCommonTitleBarTheme(mTbTitleBar)
        initIntegralRankingRecyclerView()
    }

    private fun initIntegralRankingRecyclerView() {
        mIntegralRecordAdapter.showItemAnim(AnimationType.TRANSLATE_FROM_BOTTOM, false)
        mRvIntegralRecord.apply {
            layoutManager = LinearLayoutManager(mContext)
            adapter = mIntegralRecordAdapter
        }
    }

    override fun initListener() {
        mSrlRefresh.setOnRefreshLoadMoreListener(this)
        mIntegralRankingViewModel.apply {
            mIntegralRecordUIState.mRefreshSuccess.observe(viewLifecycleOwner, Observer {
                when {
                    it -> mSrlRefresh.finishRefresh()
                }
            })
            mIntegralRecordUIState.mLoadMoreSuccess.observe(viewLifecycleOwner, Observer {
                mSrlRefresh.finishLoadMore(it)
            })
            mIntegralRecordUIState.mLoadMoreNoData.observe(viewLifecycleOwner, Observer {
                when {
                    it -> {
                        mSrlRefresh.finishLoadMoreWithNoMoreData()
                    }
                }
            })
            mIntegralRecord.observe(viewLifecycleOwner, Observer {
                it.datas?.apply {
                    when (it.curPage) {
                        1 -> {
                            mIntegralRecordAdapter.dataList.clear()
                        }
                    }
                    mIntegralRecordAdapter.dataList.addAll(this)
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
        mIntegralRankingViewModel.loadMoreIntegralRecord()
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        mIntegralRankingViewModel.refreshIntegralRecord()
    }
}