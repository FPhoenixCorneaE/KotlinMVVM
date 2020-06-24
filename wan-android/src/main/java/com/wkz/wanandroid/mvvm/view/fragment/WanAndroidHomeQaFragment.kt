package com.wkz.wanandroid.mvvm.view.fragment

import androidx.lifecycle.Observer
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import com.wkz.adapter.AnimationType
import com.wkz.adapter.BaseNBAdapter
import com.wkz.extension.navigate
import com.wkz.extension.toHtml
import com.wkz.extension.viewModel
import com.wkz.framework.web.BaseWebFragment
import com.wkz.util.BundleBuilder
import com.wkz.wanandroid.R
import com.wkz.wanandroid.mvvm.model.WanAndroidArticleBean
import com.wkz.wanandroid.mvvm.view.adapter.WanAndroidHomeQaAdapter
import com.wkz.wanandroid.mvvm.viewmodel.WanAndroidHomeQaViewModel
import kotlinx.android.synthetic.main.wan_android_fragment_home_qa.*

/**
 * @desc: 首页问答Fragment
 * @date: 2019-11-05 20:20
 */
class WanAndroidHomeQaFragment : WanAndroidBaseFragment(), OnRefreshLoadMoreListener {

    private val mHomeQaAdapter by lazy(LazyThreadSafetyMode.NONE) {
        WanAndroidHomeQaAdapter()
    }

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
                    navigate(
                        R.id.mMainToWeb,
                        BundleBuilder.of()
                            .putCharSequence(BaseWebFragment.TITLE, item.title.toHtml())
                            .putString(BaseWebFragment.WEB_URL, item.link)
                            .get()
                    )
                }
            }
        mHomeQaViewModel.apply {
            mRefreshing.observe(viewLifecycleOwner, Observer {
                when {
                    !it -> mSrlRefresh.finishRefresh()
                }
            })
            mLoadingMore.observe(viewLifecycleOwner, Observer {
                when {
                    !it -> mSrlRefresh.finishLoadMore()
                }
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
        mHomeQaAdapter.showItemAnim(AnimationType.TRANSLATE_FROM_BOTTOM, false)
        mRvQa.apply {
            setHasFixedSize(true)
            isNestedScrollingEnabled = false
            adapter = mHomeQaAdapter
        }
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