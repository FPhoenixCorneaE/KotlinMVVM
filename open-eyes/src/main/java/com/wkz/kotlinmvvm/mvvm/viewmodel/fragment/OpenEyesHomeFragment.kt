package com.wkz.kotlinmvvm.mvvm.viewmodel.fragment

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.uber.autodispose.autoDisposable
import com.wkz.framework.base.BaseFragment
import com.wkz.kotlinmvvm.R
import com.wkz.kotlinmvvm.databinding.OpenEyesFragmentHomeBinding
import com.wkz.kotlinmvvm.mvvm.contract.OpenEyesHomeContract
import com.wkz.kotlinmvvm.mvvm.model.bean.OpenEyesHomeBean
import com.wkz.kotlinmvvm.mvvm.presenter.OpenEyesHomePresenter
import com.wkz.kotlinmvvm.mvvm.viewmodel.binder.OpenEyesHomeBannerBinder
import com.wkz.kotlinmvvm.mvvm.viewmodel.binder.OpenEyesHomeDateBinder
import com.wkz.kotlinmvvm.mvvm.viewmodel.binder.OpenEyesHomeVideoBinder
import com.wkz.util.ResourceUtil
import com.wkz.util.StatusBarUtil
import io.reactivex.Observable
import kotlinx.android.synthetic.main.open_eyes_fragment_home.*
import me.drakeet.multitype.MultiTypeAdapter
import me.drakeet.multitype.withKClassLinker
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class OpenEyesHomeFragment :
    BaseFragment<OpenEyesHomeContract.View, OpenEyesHomePresenter, OpenEyesFragmentHomeBinding>(),
    OpenEyesHomeContract.View {

    private val mAdapter by lazy {
        MultiTypeAdapter()
    }

    private val mDatas by lazy {
        ArrayList<OpenEyesHomeBean.Issue.Item>()
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
        mSrlRefresh.setPrimaryColorsId(R.color.open_eyes_color_black, R.color.open_eyes_color_bg_default)

        initRecyclerView()

        // 状态栏透明和间距处理
        activity?.let {
            StatusBarUtil.darkMode(it)
            StatusBarUtil.setPaddingSmart(it, mTbToolbar)
        }
    }

    private fun initRecyclerView() {
        // 注册多状态布局
        mAdapter.register(OpenEyesHomeBean.Issue.Item::class.java).to(
            OpenEyesHomeBannerBinder(),
            OpenEyesHomeDateBinder(),
            OpenEyesHomeVideoBinder()
        ).withKClassLinker { position, data ->
            when {
                position == 0 -> OpenEyesHomeBannerBinder::class
                data.type == "textHeader" ->
                    OpenEyesHomeDateBinder::class
                else ->
                    OpenEyesHomeVideoBinder::class
            }
        }
        mAdapter.items = mDatas
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
                    if (mDatas.size > 1) {
                        mTbToolbar.setBackgroundColor(ResourceUtil.getColor(R.color.open_eyes_color_bg_title))
                        val itemList = mDatas
                        val item = itemList[currentVisibleItemPosition]
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

        val bannerItemData: ArrayList<OpenEyesHomeBean.Issue.Item> =
            mDatas.take(homeBean.issueList[0].count).toCollection(ArrayList())
        val bannerFeedList = ArrayList<String>()
        val bannerTitleList = ArrayList<String>()
        // 取出banner 显示的 img 和 Title
        Observable.fromIterable(bannerItemData)
            .autoDisposable(mScopeProvider)
            .subscribe { list ->
                bannerFeedList.add(list.data?.cover?.feed ?: "")
                bannerTitleList.add(list.data?.title ?: "")
            }
        mDatas.addAll(homeBean.issueList[0].itemList)
        mAdapter.notifyDataSetChanged()
    }

    override fun setMoreData(itemList: ArrayList<OpenEyesHomeBean.Issue.Item>) {
        mDatas.addAll(itemList)
        mAdapter.notifyDataSetChanged()
    }
}
