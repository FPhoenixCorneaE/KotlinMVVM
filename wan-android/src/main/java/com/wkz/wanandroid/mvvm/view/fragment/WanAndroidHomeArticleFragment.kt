package com.wkz.wanandroid.mvvm.view.fragment

import android.graphics.Rect
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import com.wkz.adapter.AnimationType
import com.wkz.adapter.BaseNBAdapter
import com.wkz.adapter.SimpleOnItemChildClickListener
import com.wkz.extension.navigate
import com.wkz.extension.toHtml
import com.wkz.extension.viewModel
import com.wkz.framework.web.BaseWebFragment
import com.wkz.shinebutton.ShineButton
import com.wkz.util.BundleBuilder
import com.wkz.util.SizeUtil
import com.wkz.wanandroid.R
import com.wkz.wanandroid.manager.WanAndroidUserManager
import com.wkz.wanandroid.mvvm.model.WanAndroidArticleBean
import com.wkz.wanandroid.mvvm.model.WanAndroidBannerBean
import com.wkz.wanandroid.mvvm.view.adapter.WanAndroidHomeArticleAdapter
import com.wkz.wanandroid.mvvm.view.adapter.WanAndroidHomeBannerAdapter
import com.wkz.wanandroid.mvvm.viewmodel.WanAndroidCollectViewModel
import com.wkz.wanandroid.mvvm.viewmodel.WanAndroidHomeArticleViewModel
import kotlinx.android.synthetic.main.wan_android_fragment_home_article.*

/**
 * @desc: 首页文章Fragment
 * @date: 2019-10-24 15:51
 */
class WanAndroidHomeArticleFragment : WanAndroidBaseFragment(), OnRefreshLoadMoreListener {
    private val mBannerAdapter by lazy(LazyThreadSafetyMode.NONE) {
        WanAndroidHomeBannerAdapter()
    }
    private val mHomeArticleAdapter by lazy(LazyThreadSafetyMode.NONE) {
        WanAndroidHomeArticleAdapter()
    }

    /* 首页文章ViewModel */
    private val mHomeArticleViewModel by viewModel<WanAndroidHomeArticleViewModel>()

    /* 收藏文章、网址ViewModel */
    private val mCollectViewModel by viewModel<WanAndroidCollectViewModel>()
    private var mTopArticleList = ArrayList<WanAndroidArticleBean>()

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
                    navigate(
                        R.id.mMainToWeb,
                        BundleBuilder.of()
                            .putCharSequence(BaseWebFragment.TITLE, item.title.toHtml())
                            .putString(BaseWebFragment.WEB_URL, item.url)
                            .get()
                    )
                }
            }
        mHomeArticleAdapter.apply {
            onItemClickListener =
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
            onItemChildClickListener =
                object : SimpleOnItemChildClickListener<WanAndroidArticleBean>() {
                    override fun onItemChild1Click(
                        view: View?,
                        item: WanAndroidArticleBean,
                        position: Int
                    ) {

                    }

                    override fun onItemChild2Click(
                        view: View?,
                        item: WanAndroidArticleBean,
                        position: Int
                    ) {
                        val shineButton = view as ShineButton
                        when {
                            WanAndroidUserManager.sHasLoggedOn -> {
                                // 已登录
                                when {
                                    item.collect -> {
                                        // 已收藏
                                        mCollectViewModel.cancelCollectArticle(item.id)
                                        shineButton.setChecked(false)
                                        item.collect = false
                                    }
                                    else -> {
                                        // 未收藏
                                        mCollectViewModel.collectArticle(item.id)
                                        shineButton.setChecked(true)
                                        item.collect = true
                                    }
                                }
                            }
                            else -> {
                                // 未登录,跳转登录
                                navigate(R.id.mMainToLogin)
                            }
                        }
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
                it?.apply {
                    mBannerAdapter.dataList = it
                    mBannerAdapter.notifyDataSetChanged()
                }
            })
            mTopArticleList.observe(viewLifecycleOwner, Observer {
                it?.let {
                    this@WanAndroidHomeArticleFragment.mTopArticleList = it
                }
            })
            mArticleList.observe(viewLifecycleOwner, Observer {
                it?.apply {
                    when {
                        isRefresh() -> {
                            mHomeArticleAdapter.dataList.clear()
                            mHomeArticleAdapter.dataList.addAll(
                                0,
                                this@WanAndroidHomeArticleFragment.mTopArticleList
                            )
                        }
                    }
                    mHomeArticleAdapter.dataList.addAll(datas)
                    mHomeArticleAdapter.notifyDataSetChanged()
                }
            })
        }
        mCollectViewModel.apply {
            mArticleCollect.observe(viewLifecycleOwner, Observer {
                // 收藏文章
            })
            mArticleCancelCollect.observe(viewLifecycleOwner, Observer {
                // 取消收藏文章
            })
        }
    }

    private fun initBannerRecyclerView() {
        mBannerAdapter.showItemAnim(AnimationType.ALPHA, false)
        mRvBanner.apply {
            setHasFixedSize(true)
            isNestedScrollingEnabled = false
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
        mHomeArticleAdapter.showItemAnim(AnimationType.TRANSLATE_FROM_BOTTOM, false)
        mRvArticle.apply {
            setHasFixedSize(true)
            isNestedScrollingEnabled = false
            adapter = mHomeArticleAdapter
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