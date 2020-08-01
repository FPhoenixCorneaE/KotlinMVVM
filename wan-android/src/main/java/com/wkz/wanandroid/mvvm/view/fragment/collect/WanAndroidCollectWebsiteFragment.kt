package com.wkz.wanandroid.mvvm.view.fragment.collect

import androidx.lifecycle.Observer
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import com.wkz.adapter.BaseNBAdapter
import com.wkz.extension.isNonNullAndNotEmpty
import com.wkz.extension.navigate
import com.wkz.extension.viewModel
import com.wkz.framework.web.BaseWebFragment
import com.wkz.util.BundleBuilder
import com.wkz.wanandroid.R
import com.wkz.wanandroid.mvvm.model.WanAndroidCollectWebsiteBean
import com.wkz.wanandroid.mvvm.view.adapter.WanAndroidCollectWebsiteAdapter
import com.wkz.wanandroid.mvvm.view.fragment.WanAndroidBaseFragment
import com.wkz.wanandroid.mvvm.viewmodel.WanAndroidCollectViewModel
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
                        navigate(
                            R.id.mCollectToWeb,
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