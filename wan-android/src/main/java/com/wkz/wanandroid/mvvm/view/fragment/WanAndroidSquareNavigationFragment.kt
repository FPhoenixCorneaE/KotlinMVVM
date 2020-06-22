package com.wkz.wanandroid.mvvm.view.fragment

import androidx.lifecycle.Observer
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import com.wkz.adapter.AnimationType
import com.wkz.adapter.BaseNBAdapter
import com.wkz.extension.isNonNullAndNotEmpty
import com.wkz.extension.viewModel
import com.wkz.wanandroid.R
import com.wkz.wanandroid.mvvm.model.WanAndroidNavigationBean
import com.wkz.wanandroid.mvvm.view.adapter.WanAndroidSquareNavigationAdapter
import com.wkz.wanandroid.mvvm.viewmodel.WanAndroidSquareViewModel
import kotlinx.android.synthetic.main.wan_android_fragment_square_navigation.*

/**
 * @desc: 广场导航Fragment
 * @date: 2020-06-22 10:27
 */
class WanAndroidSquareNavigationFragment : WanAndroidBaseFragment(), OnRefreshListener {

    /* 广场ViewModel */
    private val mSquareViewModel by viewModel<WanAndroidSquareViewModel>()

    /* 广场导航适配器 */
    private val mSquareNavigationAdapter by lazy(LazyThreadSafetyMode.NONE) {
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
        mSquareNavigationAdapter.showItemAnim(AnimationType.TRANSLATE_FROM_BOTTOM, false)
        mRvNavigation.apply {
            setHasFixedSize(true)
            isNestedScrollingEnabled = false
            adapter = mSquareNavigationAdapter
        }
    }

    override fun initListener() {
        mSrlRefresh.setOnRefreshListener(this)
        mSquareNavigationAdapter.onItemClickListener =
            object : BaseNBAdapter.OnItemClickListener<WanAndroidNavigationBean> {
                override fun onItemClick(item: WanAndroidNavigationBean, position: Int) {

                }
            }
        mSquareViewModel.apply {
            mNavigationDataUIState.mRefreshing.observe(viewLifecycleOwner, Observer {
                mSrlRefresh.finishRefresh()
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
        mSrlRefresh.autoRefresh()
    }

    override fun isAlreadyLoadedData(): Boolean = mSquareNavigationAdapter.dataList.isNonNullAndNotEmpty()

    override fun onRefresh(refreshLayout: RefreshLayout) {
        mSquareViewModel.getSquareNavigation()
    }
}