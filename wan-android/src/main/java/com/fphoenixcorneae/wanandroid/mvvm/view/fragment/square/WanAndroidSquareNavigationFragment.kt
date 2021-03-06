package com.fphoenixcorneae.wanandroid.mvvm.view.fragment.square

import android.view.View
import androidx.lifecycle.Observer
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import com.fphoenixcorneae.viewpager.SimpleOnItemChildClickListener
import com.fphoenixcorneae.ext.isNonNullAndNotEmpty
import com.fphoenixcorneae.ext.toHtml
import com.fphoenixcorneae.ext.viewModel
import com.fphoenixcorneae.framework.web.BaseWebFragment
import com.fphoenixcorneae.util.BundleBuilder
import com.fphoenixcorneae.wanandroid.R
import com.fphoenixcorneae.wanandroid.mvvm.model.WanAndroidNavigationBean
import com.fphoenixcorneae.wanandroid.mvvm.view.adapter.WanAndroidSquareNavigationAdapter
import com.fphoenixcorneae.wanandroid.mvvm.view.fragment.WanAndroidBaseFragment
import com.fphoenixcorneae.wanandroid.mvvm.viewmodel.WanAndroidSquareViewModel
import kotlinx.android.synthetic.main.wan_android_fragment_square_navigation.*

/**
 * @desc: 广场导航Fragment
 * @date: 2020-06-22 10:27
 */
class WanAndroidSquareNavigationFragment : WanAndroidBaseFragment(), OnRefreshListener {

    /* 广场ViewModel */
    private val mSquareViewModel by viewModel<WanAndroidSquareViewModel>()

    /* 广场导航适配器 */
    private val mSquareNavigationAdapter by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        WanAndroidSquareNavigationAdapter()
    }

    /**
     * 加载布局
     */
    override fun getLayoutId(): Int = R.layout.wan_android_fragment_square_navigation

    /**
     * 初始化View
     */
    override fun initView() {
        mSrlRefresh.setEnableLoadMore(false)
        initRecyclerView()
    }

    private fun initRecyclerView() {
        mRvNavigation.init(mSquareNavigationAdapter)
    }

    override fun initListener() {
        mSrlRefresh.setOnRefreshListener(this)
        mSquareNavigationAdapter.apply {
            onItemChildClickListener =
                object : SimpleOnItemChildClickListener<WanAndroidNavigationBean>() {
                    override fun onItemChild1Click(
                        view: View?,
                        item: WanAndroidNavigationBean,
                        position: Int
                    ) {
                        navigateNext(
                            R.id.webFragment,
                            BundleBuilder.of()
                                .putCharSequence(
                                    BaseWebFragment.TITLE,
                                    item.articles[position].title.toHtml()
                                )
                                .putString(BaseWebFragment.WEB_URL, item.articles[position].link)
                                .get()
                        )
                    }
                }
        }
        mSquareViewModel.apply {
            mNavigationDataUIState.mRefreshSuccess.observe(viewLifecycleOwner, Observer {
                mSrlRefresh.finishRefresh()
                when {
                    it -> showContent()
                    else -> showError()
                }
            })
            mSquareNavigationData.observe(viewLifecycleOwner, Observer {
                it?.apply {
                    mSquareNavigationAdapter.dataList.clear()
                    mSquareNavigationAdapter.dataList.addAll(it)
                    mSquareNavigationAdapter.notifyDataSetChanged()
                }
            })
        }
    }

    /**
     * 懒加载数据
     */
    override fun lazyLoadData() {
        mSrlRefresh.autoRefresh(400)
    }

    override fun isAlreadyLoadedData(): Boolean =
        mSquareNavigationAdapter.dataList.isNonNullAndNotEmpty()

    override fun onRefresh(refreshLayout: RefreshLayout) {
        mSquareViewModel.getSquareNavigation()
    }
}