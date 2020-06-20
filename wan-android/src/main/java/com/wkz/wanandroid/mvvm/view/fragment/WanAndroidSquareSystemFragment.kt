package com.wkz.wanandroid.mvvm.view.fragment

import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import com.wkz.adapter.AnimationType
import com.wkz.adapter.BaseNBAdapter
import com.wkz.extension.isNonNullAndNotEmpty
import com.wkz.extension.viewModel
import com.wkz.wanandroid.R
import com.wkz.wanandroid.mvvm.model.WanAndroidSystemBean
import com.wkz.wanandroid.mvvm.view.adapter.WanAndroidSquareSystemAdapter
import com.wkz.wanandroid.mvvm.viewmodel.WanAndroidSquareViewModel
import kotlinx.android.synthetic.main.wan_android_fragment_vipcn_child.*

/**
 * @desc: 广场体系Fragment
 * @date: 2020-06-20 14:35
 */
class WanAndroidSquareSystemFragment : WanAndroidBaseFragment(), OnRefreshListener {

    /* 广场ViewModel */
    private val mSquareViewModel by viewModel<WanAndroidSquareViewModel>()

    /* 广场体系适配器 */
    private val mSquareSystemAdapter by lazy(LazyThreadSafetyMode.NONE) {
        WanAndroidSquareSystemAdapter()
    }

    /**
     * 加载布局
     */
    override fun getLayoutId(): Int = R.layout.wan_android_fragment_square_system

    /**
     * 初始化View
     */
    override fun initView() {
        mSrlRefresh.setEnableLoadMore(false)
        initRecyclerView()
    }

    private fun initRecyclerView() {
        mSquareSystemAdapter.showItemAnim(AnimationType.TRANSLATE_FROM_BOTTOM, false)
        mRvVipcn.apply {
            setHasFixedSize(true)
            isNestedScrollingEnabled = false
            adapter = mSquareSystemAdapter
        }
    }

    override fun initListener() {
        mSrlRefresh.setOnRefreshListener(this)
        mSquareSystemAdapter.onItemClickListener =
            object : BaseNBAdapter.OnItemClickListener<WanAndroidSystemBean> {
                override fun onItemClick(item: WanAndroidSystemBean, position: Int) {

                }
            }
        mSquareViewModel.apply {
            mSquareSystemData.observe(viewLifecycleOwner, Observer {
                mSrlRefresh.finishRefresh()
                it?.apply {
                    mSquareSystemAdapter.dataList.clear()
                    mSquareSystemAdapter.dataList.addAll(it)
                    mSquareSystemAdapter.notifyDataSetChanged()
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

    override fun isAlreadyLoadedData(): Boolean = mSquareSystemAdapter.dataList.isNonNullAndNotEmpty()

    override fun onRefresh(refreshLayout: RefreshLayout) {
        mSquareViewModel.getSquareSystem()
    }
}