package com.wkz.wanandroid.mvvm.view.fragment.integral

import androidx.lifecycle.Observer
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import com.wkz.adapter.AnimationType
import com.wkz.extension.isNonNullAndNotEmpty
import com.wkz.extension.navigate
import com.wkz.extension.viewModel
import com.wkz.framework.web.BaseWebFragment
import com.wkz.util.BundleBuilder
import com.wkz.wanandroid.R
import com.wkz.wanandroid.api.WanAndroidUrlConstant
import com.wkz.wanandroid.mvvm.view.adapter.WanAndroidIntegralRankingAdapter
import com.wkz.wanandroid.mvvm.view.fragment.WanAndroidBaseFragment
import com.wkz.wanandroid.mvvm.viewmodel.WanAndroidIntegralViewModel
import kotlinx.android.synthetic.main.wan_android_fragment_integral_ranking.*
import kotlinx.android.synthetic.main.wan_android_layout_title_bar_integral_ranking_right.*

/**
 * @desc: 积分排行榜Fragment
 * @date: 2020-06-07 20:27
 */
class WanAndroidIntegralRankingFragment : WanAndroidBaseFragment(), OnRefreshLoadMoreListener {

    /* 积分排行榜适配器 */
    private val mIntegralRankingAdapter by lazy(LazyThreadSafetyMode.NONE) {
        WanAndroidIntegralRankingAdapter()
    }

    /* 积分ViewModel */
    private val mIntegralViewModel by viewModel<WanAndroidIntegralViewModel>()

    override fun getLayoutId(): Int = R.layout.wan_android_fragment_integral_ranking

    override fun initView() {
        // 设置标题栏主题样式
        setCommonTitleBarTheme(mTbTitleBar)
        initIntegralRankingRecyclerView()
    }

    private fun initIntegralRankingRecyclerView() {
        mIntegralRankingAdapter.showItemAnim(AnimationType.TRANSLATE_FROM_BOTTOM, false)
        mRvIntegralRanking.apply {
            setHasFixedSize(true)
            isNestedScrollingEnabled = false
            adapter = mIntegralRankingAdapter
        }
    }

    override fun initListener() {
        mSrlRefresh.setOnRefreshLoadMoreListener(this)
        mIvIntegralHelp.setOnClickListener {
            navigate(
                R.id.mIntegralRankingToWeb,
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
            navigate(R.id.mIntegralRankingToIntegralRecord)
        }
        mIntegralViewModel.apply {
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