package com.wkz.kotlinmvvm.mvvm.viewmodel.fragment

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wkz.adapter.internal.Delegation
import com.wkz.adapter.internal.MultiTypeAdapter
import com.wkz.adapter.wrapper.ViewHolderWrapper
import com.wkz.framework.base.BaseFragment
import com.wkz.kotlinmvvm.R
import com.wkz.kotlinmvvm.databinding.OpenEyesFragmentHomeBinding
import com.wkz.kotlinmvvm.mvvm.contract.OpenEyesHomeContract
import com.wkz.kotlinmvvm.mvvm.model.bean.OpenEyesHomeBean
import com.wkz.kotlinmvvm.mvvm.presenter.OpenEyesHomePresenter
import com.wkz.kotlinmvvm.mvvm.viewmodel.wrapper.OpenEyesHomeBannerWrapper
import com.wkz.kotlinmvvm.mvvm.viewmodel.wrapper.OpenEyesHomeDateWrapper
import com.wkz.kotlinmvvm.mvvm.viewmodel.wrapper.OpenEyesHomeVideoWrapper
import com.wkz.util.ResourceUtil
import com.wkz.util.StatusBarUtil
import kotlinx.android.synthetic.main.open_eyes_fragment_home.*
import java.text.SimpleDateFormat
import java.util.*

class OpenEyesHomeFragment :
    BaseFragment<OpenEyesHomeContract.View, OpenEyesHomePresenter, OpenEyesFragmentHomeBinding>(),
    OpenEyesHomeContract.View {

    private val mAdapter by lazy {
        MultiTypeAdapter()
    }

    private val mVideoWrapper by lazy {
        OpenEyesHomeVideoWrapper()
    }

    private val mLinearLayoutManager by lazy {
        LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
    }

    private val mSimpleDateFormat by lazy {
        SimpleDateFormat("- MMM. dd, 'Brunch' -", Locale.ENGLISH)
    }

    companion object {

        fun getInstance(): OpenEyesHomeFragment {
            val fragment = OpenEyesHomeFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getLayoutId(): Int = R.layout.open_eyes_fragment_home

    override fun initView() {
        // 内容跟随偏移
        mSrlRefresh.setEnableHeaderTranslationContent(true)
        mSrlRefresh.setOnRefreshListener {
            mPresenter.requestHomeData(1)
        }
        // 打开下拉刷新区域块背景:
        mMhHeader.setShowBezierWave(true)
        // 设置下拉刷新主题颜色
        mSrlRefresh.setPrimaryColorsId(
            R.color.open_eyes_color_black,
            R.color.open_eyes_color_bg_default
        )

        initRecyclerView()

        // 状态栏透明和间距处理
        activity?.let {
            StatusBarUtil.darkMode(it)
            StatusBarUtil.setPaddingSmart(it, mTbToolbar)
        }
    }

    private fun initRecyclerView() {
        mVideoWrapper.setOnItemClickListener { viewHolder, position, item ->

        }
        mVideoWrapper.setOnItemLongClickListener { viewHolder, position, item ->

            true
        }
        // 注册多状态布局
        mAdapter.register(
            OpenEyesHomeBannerWrapper(),
            OpenEyesHomeDateWrapper(),
            mVideoWrapper,
            delegation = object : Delegation<OpenEyesHomeBean.Issue.Item> {
                override fun getWrapperType(item: OpenEyesHomeBean.Issue.Item): Class<out ViewHolderWrapper<OpenEyesHomeBean.Issue.Item>> {
                    return when {
                        item.type == "textHeader" ->
                            OpenEyesHomeDateWrapper::class.java
                        item.type == "video" ->
                            OpenEyesHomeVideoWrapper::class.java
                        else ->
                            OpenEyesHomeBannerWrapper::class.java
                    }
                }
            })
        mRvRecycler.adapter = mAdapter
        mRvRecycler.layoutManager = mLinearLayoutManager
        mRvRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val childCount = mRvRecycler.childCount
                    val itemCount = mRvRecycler.layoutManager?.itemCount
                    val firstVisibleItem =
                        (mRvRecycler.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                    if (firstVisibleItem + childCount == itemCount) {
                        mPresenter.loadMoreData()
                    }
                }
            }

            /**
             * RecyclerView滚动的时候调用
             */
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val currentVisibleItemPosition = mLinearLayoutManager.findFirstVisibleItemPosition()
                if (currentVisibleItemPosition == 0) {
                    //背景设置为透明
                    mTbToolbar.setBackgroundColor(ResourceUtil.getColor(R.color.open_eyes_color_translucent))
                    mTvTitle.text = ""
                } else {
                    if (mAdapter.data.size > 1 && currentVisibleItemPosition < mAdapter.data.size) {
                        mTbToolbar.setBackgroundColor(ResourceUtil.getColor(R.color.open_eyes_color_bg_title))
                        val itemList = mAdapter.data
                        val item =
                            itemList[currentVisibleItemPosition] as OpenEyesHomeBean.Issue.Item
                        if (item.type == "textHeader") {
                            mTvTitle.text = item.data?.text
                        } else {
                            mTvTitle.text = mSimpleDateFormat.format(item.data?.date)
                        }
                    }
                }
            }
        })
    }

    override fun lazyLoadData() {
        mSrlRefresh.autoRefresh()
    }

    override fun setHomeData(homeBean: OpenEyesHomeBean) {
        mSrlRefresh.finishRefresh()

//        val bannerItemData: ArrayList<OpenEyesHomeBean.Issue.Item> =
//            (mAdapter.data.take(homeBean.issueList[0].count) as ArrayList<OpenEyesHomeBean.Issue.Item>).toCollection(
//                ArrayList()
//            )
//        val bannerFeedList = ArrayList<String>()
//        val bannerTitleList = ArrayList<String>()
//        // 取出banner 显示的 img 和 Title
//        Observable.fromIterable(bannerItemData)
//            .autoDisposable(mScopeProvider)
//            .subscribe { list ->
//                bannerFeedList.add(list.data?.cover?.feed ?: "")
//                bannerTitleList.add(list.data?.title ?: "")
//            }
        mAdapter.data.clear()
        mAdapter.data.addAll(homeBean.issueList[0].itemList)
        mAdapter.notifyDataSetChanged()
    }

    override fun setMoreData(itemList: ArrayList<OpenEyesHomeBean.Issue.Item>) {
        mAdapter.data.addAll(itemList)
        mAdapter.notifyDataSetChanged()
    }
}
