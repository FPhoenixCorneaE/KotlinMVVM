package com.wkz.wanandroid.mvvm.view.fragment

import android.graphics.Rect
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener
import com.wkz.adapter.AnimationType
import com.wkz.adapter.BaseNBAdapter
import com.wkz.extension.isNonNull
import com.wkz.extension.showToast
import com.wkz.extension.viewModel
import com.wkz.framework.base.fragment.BaseFragment
import com.wkz.util.IntentUtil
import com.wkz.util.SizeUtil
import com.wkz.wanandroid.R
import com.wkz.wanandroid.manager.WanAndroidUserManager
import com.wkz.wanandroid.mvvm.model.WanAndroidBannerBean
import com.wkz.wanandroid.mvvm.model.WanAndroidPageBean
import com.wkz.wanandroid.mvvm.view.activity.WanAndroidLoginActivity
import com.wkz.wanandroid.mvvm.view.activity.WanAndroidWebViewActivity
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
    private var mTopArticleList = ArrayList<WanAndroidPageBean.ArticleBean>()

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
        mBannerAdapter.onItemClickListener =
            object : BaseNBAdapter.OnItemClickListener<WanAndroidBannerBean> {
                override fun onItemClick(item: WanAndroidBannerBean, position: Int) {
                    WanAndroidWebViewActivity.start(mContext, item.title, item.url)
                }
            }
        mHomeArticleAdapter.onItemClickListener =
            object : BaseNBAdapter.OnItemClickListener<WanAndroidPageBean.ArticleBean> {
                override fun onItemClick(item: WanAndroidPageBean.ArticleBean, position: Int) {
                    WanAndroidWebViewActivity.start(mContext, item.title, item.link)
                }
            }
        mHomeArticleAdapter.mOnItemChildClickListener = object :
            WanAndroidHomeArticleAdapter.OnItemChildClickListener {
            override fun onCollectStatusChanged(
                view: View,
                checked: Boolean,
                data: WanAndroidPageBean.ArticleBean,
                position: Int
            ) {
                if (WanAndroidUserManager.sHasLoggedOn) {
                    // 已登录
                    showToast("已登录！")
                } else {
                    // 未登录,跳转登录
                    mHomeArticleAdapter.notifyItemChanged(position)
                    IntentUtil.startActivity(mContext, WanAndroidLoginActivity::class.java)
                }
            }
        }
        mHomeArticleViewModel.apply {
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
            mBannerList.observe(viewLifecycleOwner, Observer {
                mBannerAdapter.dataList = it
                mBannerAdapter.notifyItemRangeChanged(
                    mBannerAdapter.dataList.size - it.size - 1,
                    it.size
                )
            })
            mTopArticleList.observe(viewLifecycleOwner, Observer {
                this@WanAndroidHomeArticleFragment.mTopArticleList = it
            })
            mArticleList.observe(viewLifecycleOwner, Observer {
                when (it.curPage) {
                    1 -> {
                        mHomeArticleAdapter.dataList.clear()
                        mHomeArticleAdapter.dataList.addAll(
                            0,
                            this@WanAndroidHomeArticleFragment.mTopArticleList
                        )
                    }
                }
                if (it.datas.isNonNull()) {
                    mHomeArticleAdapter.dataList.addAll(it.datas)
                    mHomeArticleAdapter.notifyItemRangeChanged(
                        mHomeArticleAdapter.dataList.size - it.datas.size - 1,
                        it.datas.size
                    )
                }
            })
        }
    }

    private fun initBannerRecyclerView() {
        mBannerAdapter.showItemAnim(AnimationType.ALPHA, true)
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
    }

    private fun initArticleRecyclerView() {
        mHomeArticleAdapter.showItemAnim(AnimationType.TRANSLATE_FROM_BOTTOM, true)
        mRvArticle.apply {
            layoutManager = LinearLayoutManager(mContext)
            adapter = mHomeArticleAdapter
            isNestedScrollingEnabled = false
        }
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