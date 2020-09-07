package com.fphoenixcorneae.wanandroid.mvvm.view.fragment.search

import android.view.View
import androidx.lifecycle.Observer
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import com.fphoenixcorneae.adapter.BaseNBAdapter
import com.fphoenixcorneae.adapter.SimpleOnItemChildClickListener
import com.fphoenixcorneae.extension.isNonNullAndNotEmpty
import com.fphoenixcorneae.extension.toHtml
import com.fphoenixcorneae.extension.viewModel
import com.fphoenixcorneae.framework.web.BaseWebFragment
import com.fphoenixcorneae.util.BundleBuilder
import com.fphoenixcorneae.wanandroid.R
import com.fphoenixcorneae.wanandroid.constant.WanAndroidConstant
import com.fphoenixcorneae.wanandroid.mvvm.model.WanAndroidArticleBean
import com.fphoenixcorneae.wanandroid.mvvm.view.adapter.WanAndroidSearchResultAdapter
import com.fphoenixcorneae.wanandroid.mvvm.view.fragment.WanAndroidBaseFragment
import com.fphoenixcorneae.wanandroid.mvvm.viewmodel.WanAndroidSearchViewModel
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
        mTbTitleBar.init()

        mRvSearchResult.init(mSearchResultAdapter)
    }

    override fun initListener() {
        mSrlRefresh.setOnRefreshLoadMoreListener(this)
        mSearchResultAdapter.onItemClickListener =
            object : BaseNBAdapter.OnItemClickListener<WanAndroidArticleBean> {
                override fun onItemClick(item: WanAndroidArticleBean, position: Int) {
                    navigateNext(
                        R.id.webFragment,
                        BundleBuilder.of()
                            .putCharSequence(BaseWebFragment.TITLE, item.title.toHtml())
                            .putString(BaseWebFragment.WEB_URL, item.link)
                            .get()
                    )
                }
            }
        mSearchResultAdapter.onItemChildClickListener =
            object : SimpleOnItemChildClickListener<WanAndroidArticleBean>() {
                override fun onItemChild1Click(
                    view: View?,
                    item: WanAndroidArticleBean,
                    position: Int
                ) {

                }

                override fun onItemChild2Click(
                    view: View?,
                    item: WanAndroidArticleBean,
                    position: Int
                ) {

                }
            }
        mSearchViewModel.apply {
            mSearchDataUIState.mRefreshNoData.observe(viewLifecycleOwner, Observer {
                mSrlRefresh.finishRefresh()
                showEmpty()
            })
            mSearchDataUIState.mRefreshSuccess.observe(viewLifecycleOwner, Observer {
                mSrlRefresh.finishRefresh()
                when {
                    it -> showContent()
                    else -> showError()
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