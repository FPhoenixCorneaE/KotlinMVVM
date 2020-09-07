package com.fphoenixcorneae.wanandroid.mvvm.view.fragment.square

import androidx.lifecycle.Observer
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import com.fphoenixcorneae.adapter.BaseNBAdapter
import com.fphoenixcorneae.extension.isNonNullAndNotEmpty
import com.fphoenixcorneae.extension.toHtml
import com.fphoenixcorneae.extension.viewModel
import com.fphoenixcorneae.framework.web.BaseWebFragment
import com.fphoenixcorneae.util.BundleBuilder
import com.fphoenixcorneae.wanandroid.R
import com.fphoenixcorneae.wanandroid.constant.WanAndroidConstant
import com.fphoenixcorneae.wanandroid.mvvm.model.WanAndroidArticleBean
import com.fphoenixcorneae.wanandroid.mvvm.view.adapter.WanAndroidSquareArticleAdapter
import com.fphoenixcorneae.wanandroid.mvvm.view.fragment.WanAndroidBaseFragment
import com.fphoenixcorneae.wanandroid.mvvm.viewmodel.WanAndroidSquareViewModel
import kotlinx.android.synthetic.main.wan_android_fragment_square_system_article_child.*

/**
 * @desc: 体系文章子Fragment
 * @date: 2020-08-31 17:35
 */
class WanAndroidSquareSystemArticleChildFragment : WanAndroidBaseFragment(),
    OnRefreshLoadMoreListener {

    /* 广场ViewModel */
    private val mSquareViewModel by viewModel<WanAndroidSquareViewModel>()

    /* 广场文章适配器 */
    private val mSquareArticleAdapter by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        WanAndroidSquareArticleAdapter()
    }

    /* 广场体系id */
    var mSystemId = 0

    /**
     * 加载布局
     */
    override fun getLayoutId(): Int = R.layout.wan_android_fragment_square_system_article_child

    /**
     * 初始化View
     */
    override fun initView() {
        arguments?.apply {
            mSystemId = getInt(WanAndroidConstant.WAN_ANDROID_SQUARE_SYSTEM_ID)
        }
        initRecyclerView()
    }

    private fun initRecyclerView() {
        mRvSquareArticle.init(mSquareArticleAdapter)
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
            mSystemArticleDataUIState.mRefreshNoData.observe(viewLifecycleOwner, Observer {
                mSrlRefresh.finishRefresh()
                showEmpty()
            })
            mSystemArticleDataUIState.mRefreshSuccess.observe(viewLifecycleOwner, Observer {
                mSrlRefresh.finishRefresh()
                when {
                    it -> showContent()
                    else -> showError()
                }
            })
            mSystemArticleDataUIState.mLoadMoreSuccess.observe(viewLifecycleOwner, Observer {
                mSrlRefresh.finishLoadMore(it)
            })
            mSystemArticleDataUIState.mLoadMoreNoData.observe(viewLifecycleOwner, Observer {
                mSrlRefresh.finishLoadMoreWithNoMoreData()
            })
            mSquareSystemArticleData.observe(viewLifecycleOwner, Observer {
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
        mSquareViewModel.mSystemId = mSystemId
        mSrlRefresh.autoRefresh()
    }

    override fun isAlreadyLoadedData(): Boolean =
        mSquareArticleAdapter.dataList.isNonNullAndNotEmpty()

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        mSquareViewModel.loadMoreSquareSystemArticleData()
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        mSquareViewModel.refreshSquareSystemArticleData()
    }
}