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
import com.wkz.wanandroid.mvvm.viewmodel.WanAndroidMineIntegralViewModel
import kotlinx.android.synthetic.main.wan_android_fragment_integral_ranking.*

/**
 * @desc: 积分排行榜Fragment
 * @date: 2020-06-07 20:27
 */
class WanAndroidIntegralRankingFragment : WanAndroidBaseFragment(), OnRefreshLoadMoreListener {

    private val mIntegralRankingAdapter by lazy(LazyThreadSafetyMode.NONE) {
        WanAndroidMineIntegralRankingAdapter()
    }

    private val mIntegralRankingViewModel by viewModel<WanAndroidMineIntegralViewModel>()

    override fun getLayoutId(): Int = R.layout.wan_android_fragment_integral_ranking

    override fun initView() {
        // 设置标题栏主题样式
        setCommonTitleBarTheme(mTbTitleBar)
        initIntegralRankingRecyclerView()
    }

    private fun initIntegralRankingRecyclerView() {
        mIntegralRankingAdapter.showItemAnim(AnimationType.TRANSLATE_FROM_BOTTOM, false)
        mRvIntegralRanking.apply {
            layoutManager = LinearLayoutManager(mContext)
            adapter = mIntegralRankingAdapter
        }
    }

    override fun initListener() {
        mSrlRefresh.setOnRefreshLoadMoreListener(this)
        mIntegralRankingViewModel.apply {
            mRefreshingIntegralRanking.observe(viewLifecycleOwner, Observer {
                when {
                    !it -> mSrlRefresh.finishRefresh()
                }
            })
            mLoadingMoreIntegralRanking.observe(viewLifecycleOwner, Observer {
                when {
                    !it -> mSrlRefresh.finishLoadMore()
                }
            })
            mIntegralRanking.observe(viewLifecycleOwner, Observer {
                it.datas?.apply {
                    when (it.curPage) {
                        0 -> {
                            mIntegralRankingAdapter.dataList.clear()
                        }
                    }
                    mIntegralRankingAdapter.dataList.addAll(this)
                    mIntegralRankingAdapter.notifyDataSetChanged()
                }
            })
            mUserIntegral.observe(viewLifecycleOwner, Observer {
                mTvUserRanking.text = it.rank.toString()
                mTvUserName.text = it.username
                mTvCoinCount.text = it.coinCount.toString()
            })
        }
    }

    override fun lazyLoadData() {
        // 获取积分
        mIntegralRankingViewModel.getIntegral()
        mSrlRefresh.autoRefresh()
    }

    override fun isAlreadyLoadedData(): Boolean =
        mIntegralRankingAdapter.dataList.isNonNullAndNotEmpty()

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        mIntegralRankingViewModel.loadMoreIntegralRanking()
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        mIntegralRankingViewModel.refreshIntegralRanking()
    }
}