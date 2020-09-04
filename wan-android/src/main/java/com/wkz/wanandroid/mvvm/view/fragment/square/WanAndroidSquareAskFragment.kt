package com.wkz.wanandroid.mvvm.view.fragment.square

import androidx.lifecycle.Observer
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import com.wkz.adapter.BaseNBAdapter
import com.wkz.extension.isNonNullAndNotEmpty
import com.wkz.extension.toHtml
import com.wkz.extension.viewModel
import com.wkz.framework.web.BaseWebFragment
import com.wkz.util.BundleBuilder
import com.wkz.wanandroid.R
import com.wkz.wanandroid.mvvm.model.WanAndroidArticleBean
import com.wkz.wanandroid.mvvm.view.adapter.WanAndroidSquareAskAdapter
import com.wkz.wanandroid.mvvm.view.fragment.WanAndroidBaseFragment
import com.wkz.wanandroid.mvvm.viewmodel.WanAndroidSquareViewModel
import kotlinx.android.synthetic.main.wan_android_fragment_square_ask.*

/**
 * @desc: 广场问答Fragment
 * @date: 2020-06-22 14:17
 */
class WanAndroidSquareAskFragment : WanAndroidBaseFragment(), OnRefreshLoadMoreListener {

    /* 广场ViewModel */
    private val mSquareViewModel by viewModel<WanAndroidSquareViewModel>()

    /* 广场问答适配器 */
    private val mSquareAskAdapter by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        WanAndroidSquareAskAdapter()
    }

    /**
     * 加载布局
     */
    override fun getLayoutId(): Int = R.layout.wan_android_fragment_square_ask

    /**
     * 初始化View
     */
    override fun initView() {
        initRecyclerView()
    }

    private fun initRecyclerView() {
        mRvAsk.init(mSquareAskAdapter)
    }

    override fun initListener() {
        mSrlRefresh.setOnRefreshLoadMoreListener(this)
        mSquareAskAdapter.onItemClickListener =
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
            mAskDataUIState.mRefreshNoData.observe(viewLifecycleOwner, Observer {
                mSrlRefresh.finishRefresh()
                showEmpty()
            })
            mAskDataUIState.mRefreshSuccess.observe(viewLifecycleOwner, Observer {
                mSrlRefresh.finishRefresh()
                when {
                    it -> showContent()
                    else -> showError()
                }
            })
            mAskDataUIState.mLoadMoreSuccess.observe(viewLifecycleOwner, Observer {
                mSrlRefresh.finishLoadMore(it)
            })
            mAskDataUIState.mLoadMoreNoData.observe(viewLifecycleOwner, Observer {
                mSrlRefresh.finishLoadMoreWithNoMoreData()
            })
            mSquareAskData.observe(viewLifecycleOwner, Observer {
                it?.apply {
                    when {
                        isRefresh() -> {
                            mSquareAskAdapter.dataList.clear()
                        }
                    }
                    mSquareAskAdapter.dataList.addAll(it.datas)
                    mSquareAskAdapter.notifyDataSetChanged()
                }
            })
        }
    }

    /**
     * 懒加载数据
     */
    override fun lazyLoadData() {
        mSrlRefresh.autoRefresh()
    }

    override fun isAlreadyLoadedData(): Boolean =
        mSquareAskAdapter.dataList.isNonNullAndNotEmpty()

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        mSquareViewModel.loadMoreSquareAskData()
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        mSquareViewModel.refreshSquareAskData()
    }
}