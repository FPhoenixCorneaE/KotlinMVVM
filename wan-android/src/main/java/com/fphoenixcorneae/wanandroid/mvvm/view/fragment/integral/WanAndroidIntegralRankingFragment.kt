package com.fphoenixcorneae.wanandroid.mvvm.view.fragment.integral

import androidx.lifecycle.Observer
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import com.fphoenixcorneae.ext.isNonNullAndNotEmpty
import com.fphoenixcorneae.ext.viewModel
import com.fphoenixcorneae.framework.web.BaseWebFragment
import com.fphoenixcorneae.util.BundleBuilder
import com.fphoenixcorneae.wanandroid.R
import com.fphoenixcorneae.wanandroid.api.WanAndroidUrlConstant
import com.fphoenixcorneae.wanandroid.mvvm.view.adapter.WanAndroidIntegralRankingAdapter
import com.fphoenixcorneae.wanandroid.mvvm.view.fragment.WanAndroidBaseFragment
import com.fphoenixcorneae.wanandroid.mvvm.viewmodel.WanAndroidIntegralViewModel
import kotlinx.android.synthetic.main.wan_android_fragment_integral_ranking.*
import kotlinx.android.synthetic.main.wan_android_layout_title_bar_integral_ranking_right.*

/**
 * @desc: 积分排行榜Fragment
 * @date: 2020-06-07 20:27
 */
class WanAndroidIntegralRankingFragment : WanAndroidBaseFragment(), OnRefreshLoadMoreListener {

    /* 积分排行榜适配器 */
    private val mIntegralRankingAdapter by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        WanAndroidIntegralRankingAdapter()
    }

    /* 积分ViewModel */
    private val mIntegralViewModel by viewModel<WanAndroidIntegralViewModel>()

    override fun getLayoutId(): Int = R.layout.wan_android_fragment_integral_ranking

    override fun initView() {
        // 设置标题栏主题样式
        mTbTitleBar.init()
        initIntegralRankingRecyclerView()
    }

    private fun initIntegralRankingRecyclerView() {
        mRvIntegralRanking.init(mIntegralRankingAdapter)
    }

    override fun initListener() {
        mSrlRefresh.setOnRefreshLoadMoreListener(this)
        mIvIntegralHelp.setOnClickListener {
            navigateNext(
                R.id.webFragment,
                BundleBuilder.of()
                    .putCharSequence(
                        BaseWebFragment.TITLE,
                        getString(R.string.wan_android_mine_integral_rule)
                    )
                    .putString(BaseWebFragment.WEB_URL, WanAndroidUrlConstant.INTEGRAL_RULE)
                    .get()
            )
        }
        mIvIntegralRecord.setOnClickListener {
            navigateNext(R.id.integralRecordFragment)
        }
        mIntegralViewModel.apply {
            mIntegralRankingUIState.mRefreshNoData.observe(viewLifecycleOwner, Observer {
                mSrlRefresh.finishRefresh()
                showEmpty()
            })
            mIntegralRankingUIState.mRefreshSuccess.observe(viewLifecycleOwner, Observer {
                mSrlRefresh.finishRefresh()
                when {
                    it -> showContent()
                    else -> showError()
                }
            })
            mIntegralRankingUIState.mLoadMoreSuccess.observe(viewLifecycleOwner, Observer {
                mSrlRefresh.finishLoadMore(it)
            })
            mIntegralRankingUIState.mLoadMoreNoData.observe(viewLifecycleOwner, Observer {
                mSrlRefresh.finishLoadMoreWithNoMoreData()
            })
            mIntegralRanking.observe(viewLifecycleOwner, Observer {
                it?.apply {
                    when {
                        isRefresh() -> {
                            mIntegralRankingAdapter.dataList.clear()
                        }
                    }
                    mIntegralRankingAdapter.dataList.addAll(it.datas)
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
        mIntegralViewModel.getIntegral()
        mSrlRefresh.autoRefresh()
    }

    override fun isAlreadyLoadedData(): Boolean =
        mIntegralRankingAdapter.dataList.isNonNullAndNotEmpty()

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        mIntegralViewModel.loadMoreIntegralRanking()
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        mIntegralViewModel.refreshIntegralRanking()
    }
}