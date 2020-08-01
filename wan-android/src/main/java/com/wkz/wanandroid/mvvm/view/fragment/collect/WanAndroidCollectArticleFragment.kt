package com.wkz.wanandroid.mvvm.view.fragment.collect

import android.view.View
import androidx.lifecycle.Observer
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import com.wkz.adapter.BaseNBAdapter
import com.wkz.adapter.SimpleOnItemChildClickListener
import com.wkz.extension.isNonNullAndNotEmpty
import com.wkz.extension.navigate
import com.wkz.extension.toHtml
import com.wkz.extension.viewModel
import com.wkz.framework.web.BaseWebFragment
import com.wkz.util.BundleBuilder
import com.wkz.wanandroid.R
import com.wkz.wanandroid.mvvm.model.WanAndroidCollectArticleBean
import com.wkz.wanandroid.mvvm.view.adapter.WanAndroidCollectArticleAdapter
import com.wkz.wanandroid.mvvm.view.fragment.WanAndroidBaseFragment
import com.wkz.wanandroid.mvvm.viewmodel.WanAndroidCollectViewModel
import kotlinx.android.synthetic.main.wan_android_fragment_collect_article.*

/**
 * @desc: 收藏文章Fragment
 * @date: 2020-07-30 16:31
 */
class WanAndroidCollectArticleFragment : WanAndroidBaseFragment(), OnRefreshLoadMoreListener {

    /* 收藏ViewModel */
    private val mCollectViewModel by viewModel<WanAndroidCollectViewModel>()

    /* 收藏文章适配器 */
    private val mCollectArticleAdapter by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        WanAndroidCollectArticleAdapter()
    }

    override fun getLayoutId(): Int = R.layout.wan_android_fragment_collect_article

    override fun initView() {
        mRvArticle.init(mCollectArticleAdapter)
    }

    override fun initListener() {
        mSrlRefresh.setOnRefreshLoadMoreListener(this)
        mCollectArticleAdapter.apply {
            onItemClickListener =
                object : BaseNBAdapter.OnItemClickListener<WanAndroidCollectArticleBean> {
                    override fun onItemClick(item: WanAndroidCollectArticleBean, position: Int) {
                        navigate(
                            R.id.mCollectToWeb,
                            BundleBuilder.of()
                                .putCharSequence(BaseWebFragment.TITLE, item.title.toHtml())
                                .putString(BaseWebFragment.WEB_URL, item.link)
                                .get()
                        )
                    }
                }
            onItemChildClickListener =
                object : SimpleOnItemChildClickListener<WanAndroidCollectArticleBean>() {
                    override fun onItemChild1Click(
                        view: View?,
                        item: WanAndroidCollectArticleBean,
                        position: Int
                    ) {
                        // 作者点击
                    }
                }
        }
        mCollectViewModel.apply {
            mCollectArticleDataUIState.mRefreshNoData.observe(viewLifecycleOwner, Observer {
                mSrlRefresh.finishRefresh()
                showEmpty()
            })
            mCollectArticleDataUIState.mRefreshSuccess.observe(viewLifecycleOwner, Observer {
                mSrlRefresh.finishRefresh()
                when {
                    it -> showContent()
                    else -> showError()
                }
            })
            mCollectArticleDataUIState.mLoadMoreSuccess.observe(viewLifecycleOwner, Observer {
                mSrlRefresh.finishLoadMore(it)
            })
            mCollectArticleDataUIState.mLoadMoreNoData.observe(viewLifecycleOwner, Observer {
                mSrlRefresh.finishLoadMoreWithNoMoreData()
            })
            mCollectArticleData.observe(viewLifecycleOwner, Observer {
                it?.apply {
                    when {
                        isRefresh() -> {
                            mCollectArticleAdapter.dataList.clear()
                        }
                    }
                    mCollectArticleAdapter.dataList.addAll(it.datas)
                    mCollectArticleAdapter.notifyDataSetChanged()
                }
            })
            mCollectArticleData.observe(viewLifecycleOwner, Observer {

            })
        }
    }

    override fun lazyLoadData() {
        mSrlRefresh.autoRefresh()
    }

    override fun isAlreadyLoadedData(): Boolean =
        mCollectArticleAdapter.dataList.isNonNullAndNotEmpty()

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        mCollectViewModel.loadMoreCollectArticleData()
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        mCollectViewModel.refreshCollectArticleData()
    }
}