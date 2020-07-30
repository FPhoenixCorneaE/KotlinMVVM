package com.wkz.wanandroid.mvvm.view.fragment.search

import androidx.lifecycle.Observer
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import com.wkz.extension.isNonNullAndNotEmpty
import com.wkz.extension.viewModel
import com.wkz.wanandroid.R
import com.wkz.wanandroid.constant.WanAndroidConstant
import com.wkz.wanandroid.mvvm.view.adapter.WanAndroidSearchResultAdapter
import com.wkz.wanandroid.mvvm.view.fragment.WanAndroidBaseFragment
import com.wkz.wanandroid.mvvm.viewmodel.WanAndroidSearchViewModel
import kotlinx.android.synthetic.main.wan_android_fragment_search_result.*

/**
 * @desc：搜索结果Fragment
 * @date：2020-07-27 10:00
 */
class WanAndroidSearchResultFragment : WanAndroidBaseFragment(), OnRefreshLoadMoreListener {

    /* 搜索ViewModel */
    private val mSearchViewModel by viewModel<WanAndroidSearchViewModel>()
    private var mSearchKey = ""
    private val mSearchResultAdapter by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        WanAndroidSearchResultAdapter()
    }

    override fun getLayoutId(): Int = R.layout.wan_android_fragment_search_result

    override fun initView() {
        mSearchKey = arguments?.getString(WanAndroidConstant.WAN_ANDROID_SEARCH_KEY) ?: ""
        mTbTitleBar.centerTextView?.text = mSearchKey
        setCommonTitleBarTheme(mTbTitleBar)

        mRvSearchResult.init(mSearchResultAdapter)
    }

    override fun initListener() {
        mSrlRefresh.setOnRefreshLoadMoreListener(this)
        mSearchViewModel.apply {
            mSearchDataUIState.mRefreshNoData.observe(viewLifecycleOwner, Observer {
                mSrlRefresh.finishRefresh()
                showEmpty()
            })
            mSearchDataUIState.mRefreshSuccess.observe(viewLifecycleOwner, Observer {
                mSrlRefresh.finishRefresh()
                when {
                    !it -> showError()
                }
            })
            mSearchDataUIState.mLoadMoreSuccess.observe(viewLifecycleOwner, Observer {
                mSrlRefresh.finishLoadMore(it)
            })
            mSearchDataUIState.mLoadMoreNoData.observe(viewLifecycleOwner, Observer {
                mSrlRefresh.finishLoadMoreWithNoMoreData()
            })
            mSearchData.observe(viewLifecycleOwner, Observer {
                it?.apply {
                    when {
                        isRefresh() -> {
                            mSearchResultAdapter.dataList.clear()
                        }
                    }
                    mSearchResultAdapter.dataList.addAll(it.datas)
                    mSearchResultAdapter.notifyDataSetChanged()
                }
            })
        }
    }

    override fun lazyLoadData() {
        mSrlRefresh.autoRefresh()
    }

    override fun isAlreadyLoadedData(): Boolean =
        mSearchResultAdapter.dataList.isNonNullAndNotEmpty()

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        mSearchViewModel.loadMoreSearchDataByKey(mSearchKey)
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        mSearchViewModel.refreshSearchDataByKey(mSearchKey)
    }
}