package com.fphoenixcorneae.wanandroid.mvvm.view.fragment.vipcn

import androidx.lifecycle.Observer
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import com.fphoenixcorneae.adapter.BaseNBAdapter
import com.fphoenixcorneae.ext.isNonNullAndNotEmpty
import com.fphoenixcorneae.ext.toHtml
import com.fphoenixcorneae.ext.viewModel
import com.fphoenixcorneae.framework.web.BaseWebFragment
import com.fphoenixcorneae.util.BundleBuilder
import com.fphoenixcorneae.wanandroid.R
import com.fphoenixcorneae.wanandroid.constant.WanAndroidConstant
import com.fphoenixcorneae.wanandroid.mvvm.model.WanAndroidArticleBean
import com.fphoenixcorneae.wanandroid.mvvm.view.adapter.WanAndroidVipcnAdapter
import com.fphoenixcorneae.wanandroid.mvvm.view.fragment.WanAndroidBaseFragment
import com.fphoenixcorneae.wanandroid.mvvm.viewmodel.WanAndroidVipcnViewModel
import kotlinx.android.synthetic.main.wan_android_fragment_vipcn_child.*

/**
 * @desc: 公众号子Fragment
 * @date: 2020-06-18 17:42
 */
class WanAndroidVipcnChildFragment : WanAndroidBaseFragment(), OnRefreshLoadMoreListener {

    /* 公众号ViewModel */
    private val mVipcnViewModel by viewModel<WanAndroidVipcnViewModel>()

    /* 公众号适配器 */
    private val mVipcnAdapter by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        WanAndroidVipcnAdapter()
    }

    /* 公众号分类id */
    private var mClassifyId = 0

    /**
     * 加载布局
     */
    override fun getLayoutId(): Int = R.layout.wan_android_fragment_vipcn_child

    /**
     * 初始化View
     */
    override fun initView() {
        arguments?.apply {
            mClassifyId = getInt(WanAndroidConstant.WAN_ANDROID_CLASSIFY_ID)
        }
        initRecyclerView()
    }

    private fun initRecyclerView() {
        mRvVipcn.init(mVipcnAdapter)
    }

    override fun initListener() {
        mSrlRefresh.setOnRefreshLoadMoreListener(this)
        mVipcnAdapter.onItemClickListener =
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
        mVipcnViewModel.apply {
            mDataUIState.mRefreshNoData.observe(viewLifecycleOwner, Observer {
                mSrlRefresh.finishRefresh()
                showEmpty()
            })
            mDataUIState.mRefreshSuccess.observe(viewLifecycleOwner, Observer {
                mSrlRefresh.finishRefresh()
                when {
                    it -> showContent()
                    else -> showError()
                }
            })
            mDataUIState.mLoadMoreSuccess.observe(viewLifecycleOwner, Observer {
                mSrlRefresh.finishLoadMore(it)
            })
            mDataUIState.mLoadMoreNoData.observe(viewLifecycleOwner, Observer {
                mSrlRefresh.finishLoadMoreWithNoMoreData()
            })
            mVipcnData.observe(viewLifecycleOwner, Observer {
                it?.apply {
                    when {
                        isRefresh() -> {
                            mVipcnAdapter.dataList.clear()
                        }
                    }
                    mVipcnAdapter.dataList.addAll(it.datas)
                    mVipcnAdapter.notifyDataSetChanged()
                }
            })
        }
    }

    /**
     * 懒加载数据
     */
    override fun lazyLoadData() {
        mVipcnViewModel.mClassifyId = mClassifyId
        mSrlRefresh.autoRefresh()
    }

    override fun isAlreadyLoadedData(): Boolean = mVipcnAdapter.dataList.isNonNullAndNotEmpty()

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        mVipcnViewModel.loadMoreVipcnData()
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        mVipcnViewModel.refreshVipcnData()
    }
}