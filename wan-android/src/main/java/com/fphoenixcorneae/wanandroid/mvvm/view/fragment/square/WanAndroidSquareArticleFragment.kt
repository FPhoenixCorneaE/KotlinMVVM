package com.fphoenixcorneae.wanandroid.mvvm.view.fragment.square

import androidx.lifecycle.Observer
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import com.fphoenixcorneae.viewpager.BaseNBAdapter
import com.fphoenixcorneae.ext.isNonNullAndNotEmpty
import com.fphoenixcorneae.ext.toHtml
import com.fphoenixcorneae.ext.viewModel
import com.fphoenixcorneae.framework.web.BaseWebFragment
import com.fphoenixcorneae.util.BundleBuilder
import com.fphoenixcorneae.wanandroid.R
import com.fphoenixcorneae.wanandroid.mvvm.model.WanAndroidArticleBean
import com.fphoenixcorneae.wanandroid.mvvm.view.adapter.WanAndroidSquareArticleAdapter
import com.fphoenixcorneae.wanandroid.mvvm.view.fragment.WanAndroidBaseFragment
import com.fphoenixcorneae.wanandroid.mvvm.viewmodel.WanAndroidSquareViewModel
import kotlinx.android.synthetic.main.wan_android_fragment_square_article.*

/**
 * @desc: 广场文章Fragment
 * @date: 2020-06-24 16:17
 */
class WanAndroidSquareArticleFragment : WanAndroidBaseFragment(), OnRefreshLoadMoreListener {

    /* 广场ViewModel */
    private val mSquareViewModel by viewModel<WanAndroidSquareViewModel>()

    /* 广场文章适配器 */
    private val mSquareArticleAdapter by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        WanAndroidSquareArticleAdapter()
    }

    /**
     * 加载布局
     */
    override fun getLayoutId(): Int = R.layout.wan_android_fragment_square_article

    /**
     * 初始化View
     */
    override fun initView() {
        initRecyclerView()
    }

    private fun initRecyclerView() {
        mRvArticle.init(mSquareArticleAdapter)
    }

    override fun initListener() {
        mSrlRefresh.setOnRefreshLoadMoreListener(this)
        mSquareArticleAdapter.onItemClickListener =
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
        mSquareViewModel.apply {
            mArticleDataUIState.mRefreshNoData.observe(viewLifecycleOwner, Observer {
                mSrlRefresh.finishRefresh()
                showEmpty()
            })
            mArticleDataUIState.mRefreshSuccess.observe(viewLifecycleOwner, Observer {
                mSrlRefresh.finishRefresh()
                when {
                    it -> showContent()
                    else -> showError()
                }
            })
            mArticleDataUIState.mLoadMoreSuccess.observe(viewLifecycleOwner, Observer {
                mSrlRefresh.finishLoadMore(it)
            })
            mArticleDataUIState.mLoadMoreNoData.observe(viewLifecycleOwner, Observer {
                mSrlRefresh.finishLoadMoreWithNoMoreData()
            })
            mSquareArticleData.observe(viewLifecycleOwner, Observer {
                it?.apply {
                    when {
                        isRefresh() -> {
                            mSquareArticleAdapter.dataList.clear()
                        }
                    }
                    mSquareArticleAdapter.dataList.addAll(it.datas)
                    mSquareArticleAdapter.notifyDataSetChanged()
                }
            })
        }
    }

    /**
     * 懒加载数据
     */
    override fun lazyLoadData() {
        mSrlRefresh.autoRefresh(400)
    }

    override fun isAlreadyLoadedData(): Boolean =
        mSquareArticleAdapter.dataList.isNonNullAndNotEmpty()

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        mSquareViewModel.loadMoreSquareArticleData()
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        mSquareViewModel.refreshSquareArticleData()
    }
}