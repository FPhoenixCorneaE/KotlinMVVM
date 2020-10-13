package com.fphoenixcorneae.wanandroid.mvvm.view.fragment.collect

import androidx.lifecycle.Observer
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import com.fphoenixcorneae.viewpager.BaseNBAdapter
import com.fphoenixcorneae.ext.isNonNullAndNotEmpty
import com.fphoenixcorneae.ext.viewModel
import com.fphoenixcorneae.framework.web.BaseWebFragment
import com.fphoenixcorneae.util.BundleBuilder
import com.fphoenixcorneae.wanandroid.R
import com.fphoenixcorneae.wanandroid.mvvm.model.WanAndroidCollectWebsiteBean
import com.fphoenixcorneae.wanandroid.mvvm.view.adapter.WanAndroidCollectWebsiteAdapter
import com.fphoenixcorneae.wanandroid.mvvm.view.fragment.WanAndroidBaseFragment
import com.fphoenixcorneae.wanandroid.mvvm.viewmodel.WanAndroidCollectViewModel
import kotlinx.android.synthetic.main.wan_android_fragment_collect_website.*

/**
 * @desc: 收藏网址Fragment
 * @date: 2020-07-30 16:31
 */
class WanAndroidCollectWebsiteFragment : WanAndroidBaseFragment(), OnRefreshListener {

    /* 收藏ViewModel */
    private val mCollectViewModel by viewModel<WanAndroidCollectViewModel>()

    /* 收藏网址适配器 */
    private val mCollectWebsiteAdapter by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        WanAndroidCollectWebsiteAdapter()
    }

    override fun getLayoutId(): Int = R.layout.wan_android_fragment_collect_website

    override fun initView() {
        mRvWebsite.init(mCollectWebsiteAdapter)
    }

    override fun initListener() {
        mSrlRefresh.setOnRefreshListener(this)
        mCollectWebsiteAdapter.apply {
            onItemClickListener =
                object : BaseNBAdapter.OnItemClickListener<WanAndroidCollectWebsiteBean> {
                    override fun onItemClick(item: WanAndroidCollectWebsiteBean, position: Int) {
                        navigateNext(
                            R.id.webFragment,
                            BundleBuilder.of()
                                .putCharSequence(BaseWebFragment.TITLE, item.name)
                                .putString(BaseWebFragment.WEB_URL, item.link)
                                .get()
                        )
                    }
                }
        }
        mCollectViewModel.apply {
            mCollectWebsiteDataUIState.mRefreshNoData.observe(viewLifecycleOwner, Observer {
                mSrlRefresh.finishRefresh()
                showEmpty()
            })
            mCollectWebsiteDataUIState.mRefreshSuccess.observe(viewLifecycleOwner, Observer {
                mSrlRefresh.finishRefresh()
                when {
                    it -> showContent()
                    else -> showError()
                }
            })
            mCollectWebsiteDataUIState.mLoadMoreSuccess.observe(viewLifecycleOwner, Observer {
                mSrlRefresh.finishLoadMore(it)
            })
            mCollectWebsiteDataUIState.mLoadMoreNoData.observe(viewLifecycleOwner, Observer {
                mSrlRefresh.finishLoadMoreWithNoMoreData()
            })
            mCollectWebsiteData.observe(viewLifecycleOwner, Observer {
                it?.apply {
                    mCollectWebsiteAdapter.dataList.clear()
                    mCollectWebsiteAdapter.dataList.addAll(this)
                    mCollectWebsiteAdapter.notifyDataSetChanged()
                }
            })
        }
    }

    override fun lazyLoadData() {
        mSrlRefresh.autoRefresh()
    }

    override fun isAlreadyLoadedData(): Boolean =
        mCollectWebsiteAdapter.dataList.isNonNullAndNotEmpty()

    override fun onRefresh(refreshLayout: RefreshLayout) {
        mCollectViewModel.getCollectWebsiteData()
    }
}