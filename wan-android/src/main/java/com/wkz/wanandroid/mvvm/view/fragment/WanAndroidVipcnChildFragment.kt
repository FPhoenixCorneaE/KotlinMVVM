package com.wkz.wanandroid.mvvm.view.fragment

import androidx.lifecycle.Observer
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import com.wkz.adapter.AnimationType
import com.wkz.adapter.BaseNBAdapter
import com.wkz.extension.isNonNullAndNotEmpty
import com.wkz.extension.navigate
import com.wkz.extension.toHtml
import com.wkz.extension.viewModel
import com.wkz.framework.web.BaseWebFragment
import com.wkz.util.BundleBuilder
import com.wkz.wanandroid.R
import com.wkz.wanandroid.constant.WanAndroidConstant
import com.wkz.wanandroid.mvvm.model.WanAndroidArticleBean
import com.wkz.wanandroid.mvvm.view.adapter.WanAndroidVipcnAdapter
import com.wkz.wanandroid.mvvm.viewmodel.WanAndroidVipcnViewModel
import kotlinx.android.synthetic.main.wan_android_fragment_vipcn_child.*

/**
 * @desc: 公众号子Fragment
 * @date: 2020-06-18 17:42
 */
class WanAndroidVipcnChildFragment : WanAndroidBaseFragment(), OnRefreshLoadMoreListener {

    /* 公众号ViewModel */
    private val mVipcnViewModel by viewModel<WanAndroidVipcnViewModel>()

    /* 公众号适配器 */
    private val mVipcnAdapter by lazy(LazyThreadSafetyMode.NONE) {
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
        mVipcnAdapter.showItemAnim(AnimationType.TRANSLATE_FROM_BOTTOM, false)
        mRvVipcn.apply {
            setHasFixedSize(true)
            isNestedScrollingEnabled = false
            adapter = mVipcnAdapter
        }
    }

    override fun initListener() {
        mSrlRefresh.setOnRefreshLoadMoreListener(this)
        mVipcnAdapter.onItemClickListener =
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
        mVipcnViewModel.apply {
            mDataUIState.mRefreshNoData.observe(viewLifecycleOwner, Observer {
                mSrlRefresh.finishRefresh()
                showEmpty()
            })
            mDataUIState.mRefreshSuccess.observe(viewLifecycleOwner, Observer {
                mSrlRefresh.finishRefresh()
                when {
                    !it -> showError()
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