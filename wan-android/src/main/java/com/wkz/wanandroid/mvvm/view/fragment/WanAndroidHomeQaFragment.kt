package com.wkz.wanandroid.mvvm.view.fragment

import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener
import com.wkz.adapter.AnimationType
import com.wkz.adapter.BaseNBAdapter
import com.wkz.extension.isNonNull
import com.wkz.extension.viewModel
import com.wkz.framework.base.fragment.BaseFragment
import com.wkz.wanandroid.R
import com.wkz.wanandroid.mvvm.model.WanAndroidPageBean
import com.wkz.wanandroid.mvvm.view.activity.WanAndroidWebViewActivity
import com.wkz.wanandroid.mvvm.view.adapter.WanAndroidHomeQaAdapter
import com.wkz.wanandroid.mvvm.viewmodel.WanAndroidHomeQaViewModel
import kotlinx.android.synthetic.main.wan_android_fragment_home_qa.*

/**
 * @desc: 首页问答Fragment
 * @date: 2019-11-05 20:20
 */
class WanAndroidHomeQaFragment : BaseFragment(), OnRefreshLoadMoreListener {

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
            object : BaseNBAdapter.OnItemClickListener<WanAndroidPageBean.ArticleBean> {
                override fun onItemClick(item: WanAndroidPageBean.ArticleBean, position: Int) {
                    WanAndroidWebViewActivity.start(mContext, item.title, item.link)
                }
            }
        mHomeQaViewModel.apply {
            mRefreshing.observe(mContext, Observer {
                when {
                    !it -> mSrlRefresh.finishRefresh()
                }
            })
            mLoadingMore.observe(mContext, Observer {
                when {
                    !it -> mSrlRefresh.finishLoadMore()
                }
            })
            mQaList.observe(mContext, Observer {
                when (it.curPage) {
                    1 -> mHomeQaAdapter.dataList.clear()
                }
                if (it.datas.isNonNull()) {
                    mHomeQaAdapter.dataList.addAll(it.datas)
                    mHomeQaAdapter.notifyItemRangeChanged(
                        mHomeQaAdapter.dataList.size - it.datas.size - 1,
                        it.datas.size
                    )
                }
            })
        }
    }

    private fun initQaRecyclerView() {
        mHomeQaAdapter.showItemAnim(AnimationType.TRANSLATE_FROM_BOTTOM, true)
        mRvQa.apply {
            layoutManager = LinearLayoutManager(mContext)
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