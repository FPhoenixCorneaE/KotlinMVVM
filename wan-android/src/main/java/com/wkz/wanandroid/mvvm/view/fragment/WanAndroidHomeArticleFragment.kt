package com.wkz.wanandroid.mvvm.view.fragment

import android.graphics.Rect
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener
import com.wkz.adapter.AnimationType
import com.wkz.extension.viewModel
import com.wkz.framework.base.fragment.BaseFragment
import com.wkz.util.SizeUtil
import com.wkz.wanandroid.R
import com.wkz.wanandroid.mvvm.model.WanAndroidBannerBean
import com.wkz.wanandroid.mvvm.view.adapter.WanAndroidHomeArticleAdapter
import com.wkz.wanandroid.mvvm.view.adapter.WanAndroidHomeBannerAdapter
import com.wkz.wanandroid.mvvm.viewmodel.WanAndroidHomeArticleViewModel
import kotlinx.android.synthetic.main.wan_android_fragment_home_article.*

/**
 * @desc: 首页文章Fragment
 * @date: 2019-10-24 15:51
 */
class WanAndroidHomeArticleFragment : BaseFragment(), OnRefreshLoadMoreListener {
    private val mBannerAdapter by lazy(LazyThreadSafetyMode.NONE) {
        WanAndroidHomeBannerAdapter()
    }

    private val mHomeArticleAdapter by lazy(LazyThreadSafetyMode.NONE) {
        WanAndroidHomeArticleAdapter()
    }

    private val mHomeArticleViewModel by viewModel<WanAndroidHomeArticleViewModel>()

    /**
     * 加载布局
     */
    override fun getLayoutId(): Int = R.layout.wan_android_fragment_home_article

    /**
     * 初始化 View
     */
    override fun initView() {
        initBannerRecyclerView()
        initArticleRecyclerView()
    }

    override fun initListener() {
        mSrlRefresh.setOnRefreshLoadMoreListener(this)
        mHomeArticleViewModel.mRefreshing.observe(this, Observer {
            when {
                !it -> mSrlRefresh.finishRefresh(1500)
            }
        })
        mHomeArticleViewModel.mLoadingMore.observe(this, Observer {
            when {
                !it -> mSrlRefresh.finishLoadMore(1500)
            }
        })
    }

    private fun initBannerRecyclerView() {
        mBannerAdapter.showItemAnim(AnimationType.ALPHA)
        mRvBanner.apply {
            adapter = mBannerAdapter
            stayEnd(false)
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    super.getItemOffsets(outRect, view, parent, state)
                    if ((view.layoutParams as RecyclerView.LayoutParams).viewLayoutPosition == 0) {
                        outRect.left = SizeUtil.dp2px(8F)
                    } else {
                        outRect.left = 0
                    }
                    outRect.right = SizeUtil.dp2px(8F)
                }
            })
        }
        mHomeArticleViewModel.mBannerList.observe(this, Observer {
            mBannerAdapter.dataList = it as ArrayList<WanAndroidBannerBean>
            mBannerAdapter.notifyDataSetChanged()
        })
    }

    private fun initArticleRecyclerView() {
        mHomeArticleAdapter.showItemAnim(AnimationType.TRANSLATE_FROM_BOTTOM)
        mRvArticle.apply {
            layoutManager = LinearLayoutManager(mContext)
            adapter = mHomeArticleAdapter
            isNestedScrollingEnabled = false
        }
        mHomeArticleViewModel.mArticleList.observe(this, Observer {
            when {
                it.curPage == 1 -> mHomeArticleAdapter.dataList.clear()
            }
            mHomeArticleAdapter.dataList.addAll(it.datas)
            mHomeArticleAdapter.notifyDataSetChanged()
        })
    }

    /**
     * 懒加载数据
     */
    override fun lazyLoadData() {
        mSrlRefresh.autoRefresh()
    }

    override fun onLoadMore(refreshlayout: RefreshLayout) {
        mHomeArticleViewModel.loadMore()
    }

    override fun onRefresh(refreshlayout: RefreshLayout) {
        mHomeArticleViewModel.autoRefresh()
    }

    override fun isAlreadyLoadedData(): Boolean = true
}