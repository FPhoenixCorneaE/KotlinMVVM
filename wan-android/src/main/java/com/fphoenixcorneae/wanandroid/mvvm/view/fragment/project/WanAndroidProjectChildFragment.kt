package com.fphoenixcorneae.wanandroid.mvvm.view.fragment.project

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
import com.fphoenixcorneae.wanandroid.mvvm.view.adapter.WanAndroidProjectAdapter
import com.fphoenixcorneae.wanandroid.mvvm.view.fragment.WanAndroidBaseFragment
import com.fphoenixcorneae.wanandroid.mvvm.viewmodel.WanAndroidProjectViewModel
import kotlinx.android.synthetic.main.wan_android_fragment_project_child.*

/**
 * @desc: 项目子Fragment
 * @date: 2020-06-14 17:31
 */
class WanAndroidProjectChildFragment : WanAndroidBaseFragment(), OnRefreshLoadMoreListener {

    /* 项目ViewModel */
    private val mProjectViewModel by viewModel<WanAndroidProjectViewModel>()

    /* 项目适配器 */
    private val mProjectAdapter by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        WanAndroidProjectAdapter()
    }

    /* 项目分类id */
    private var mClassifyId = 0

    /* 是否是最新的 */
    private var mIsNewest = false

    /**
     * 加载布局
     */
    override fun getLayoutId(): Int = R.layout.wan_android_fragment_project_child

    /**
     * 初始化View
     */
    override fun initView() {
        arguments?.apply {
            mClassifyId = getInt(WanAndroidConstant.WAN_ANDROID_CLASSIFY_ID)
            mIsNewest = getBoolean(WanAndroidConstant.WAN_ANDROID_NEWEST_PROJECT)
        }
        initRecyclerView()
    }

    private fun initRecyclerView() {
        mRvProject.init(mProjectAdapter)
    }

    override fun initListener() {
        mSrlRefresh.setOnRefreshLoadMoreListener(this)
        mProjectAdapter.onItemClickListener =
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
        mProjectViewModel.apply {
            mNewestDataUIState.mRefreshNoData.observe(viewLifecycleOwner, Observer {
                mSrlRefresh.finishRefresh()
                showEmpty()
            })
            mNewestDataUIState.mRefreshSuccess.observe(viewLifecycleOwner, Observer {
                mSrlRefresh.finishRefresh()
                when {
                    it -> showContent()
                    else -> showError()
                }
            })
            mNewestDataUIState.mLoadMoreSuccess.observe(viewLifecycleOwner, Observer {
                mSrlRefresh.finishLoadMore(it)
            })
            mNewestDataUIState.mLoadMoreNoData.observe(viewLifecycleOwner, Observer {
                mSrlRefresh.finishLoadMoreWithNoMoreData()
            })
            mProjectNewestData.observe(viewLifecycleOwner, Observer {
                it?.apply {
                    when {
                        isRefresh() -> {
                            mProjectAdapter.dataList.clear()
                        }
                    }
                    mProjectAdapter.dataList.addAll(it.datas)
                    mProjectAdapter.notifyDataSetChanged()
                }
            })
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
            mProjectData.observe(viewLifecycleOwner, Observer {
                it?.apply {
                    when {
                        isRefresh() -> {
                            mProjectAdapter.dataList.clear()
                        }
                    }
                    mProjectAdapter.dataList.addAll(it.datas)
                    mProjectAdapter.notifyDataSetChanged()
                }
            })
        }
    }

    /**
     * 懒加载数据
     */
    override fun lazyLoadData() {
        mProjectViewModel.mClassifyId = mClassifyId
        mSrlRefresh.autoRefresh()
    }

    override fun isAlreadyLoadedData(): Boolean = mProjectAdapter.dataList.isNonNullAndNotEmpty()

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        mProjectViewModel.loadMoreProjectData(mIsNewest)
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        mProjectViewModel.refreshProjectData(mIsNewest)
    }
}