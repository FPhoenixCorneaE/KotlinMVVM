package com.fphoenixcorneae.wanandroid.mvvm.view.fragment.home

import androidx.lifecycle.Observer
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import com.fphoenixcorneae.adapter.BaseNBAdapter
import com.fphoenixcorneae.ext.toHtml
import com.fphoenixcorneae.ext.viewModel
import com.fphoenixcorneae.framework.web.BaseWebFragment
import com.fphoenixcorneae.util.BundleBuilder
import com.fphoenixcorneae.wanandroid.R
import com.fphoenixcorneae.wanandroid.mvvm.model.WanAndroidArticleBean
import com.fphoenixcorneae.wanandroid.mvvm.view.adapter.WanAndroidHomeQaAdapter
import com.fphoenixcorneae.wanandroid.mvvm.view.fragment.WanAndroidBaseFragment
import com.fphoenixcorneae.wanandroid.mvvm.viewmodel.WanAndroidHomeQaViewModel
import kotlinx.android.synthetic.main.wan_android_fragment_home_qa.*

/**
 * @desc: 首页问答Fragment
 * @date: 2019-11-05 20:20
 */
class WanAndroidHomeQaFragment : WanAndroidBaseFragment(), OnRefreshLoadMoreListener {

    private val mHomeQaAdapter by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        WanAndroidHomeQaAdapter()
    }

    /* 首页问答ViewModel */
    private val mHomeQaViewModel by viewModel<WanAndroidHomeQaViewModel>()

    /**
     * 加载布局
     */
    override fun getLayoutId(): Int = R.layout.wan_android_fragment_home_qa

    /**
     * 初始化 View
     */
    override fun initView() {
        initQaRecyclerView()
    }

    override fun initListener() {
        mSrlRefresh.setOnRefreshLoadMoreListener(this)
        mHomeQaAdapter.onItemClickListener =
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
        mHomeQaViewModel.apply {
            mQaDataUIState.mRefreshNoData.observe(viewLifecycleOwner, Observer {
                mSrlRefresh.finishRefresh()
                showEmpty()
            })
            mQaDataUIState.mRefreshSuccess.observe(viewLifecycleOwner, Observer {
                mSrlRefresh.finishRefresh()
                when {
                    it -> showContent()
                    else -> showError()
                }
            })
            mQaDataUIState.mLoadMoreSuccess.observe(viewLifecycleOwner, Observer {
                mSrlRefresh.finishLoadMore(it)
            })
            mQaDataUIState.mLoadMoreNoData.observe(viewLifecycleOwner, Observer {
                mSrlRefresh.finishLoadMoreWithNoMoreData()
            })
            mQaList.observe(viewLifecycleOwner, Observer {
                it?.apply {
                    when {
                        isRefresh() -> mHomeQaAdapter.dataList.clear()
                    }
                    mHomeQaAdapter.dataList.addAll(datas)
                    mHomeQaAdapter.notifyDataSetChanged()
                }
            })
        }
    }

    private fun initQaRecyclerView() {
        mRvQa.init(mHomeQaAdapter)
    }

    /**
     * 懒加载数据
     */
    override fun lazyLoadData() {
        mSrlRefresh.autoRefresh()
    }

    override fun onLoadMore(refreshlayout: RefreshLayout) {
        mHomeQaViewModel.loadMore()
    }

    override fun onRefresh(refreshlayout: RefreshLayout) {
        mHomeQaViewModel.autoRefresh()
    }

    override fun isAlreadyLoadedData(): Boolean = true
}