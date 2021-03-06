package com.fphoenixcorneae.openeyes.mvvm.viewmodel.fragment

import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fphoenixcorneae.viewpager.internal.Delegation
import com.fphoenixcorneae.viewpager.internal.MultiTypeAdapter
import com.fphoenixcorneae.viewpager.wrapper.ViewHolderWrapper
import com.fphoenixcorneae.ext.view.gone
import com.fphoenixcorneae.ext.view.visible
import com.fphoenixcorneae.openeyes.R
import com.fphoenixcorneae.openeyes.constant.OpenEyesConstants
import com.fphoenixcorneae.openeyes.mvvm.contract.OpenEyesHomeContract
import com.fphoenixcorneae.openeyes.mvvm.model.bean.OpenEyesHomeBean
import com.fphoenixcorneae.openeyes.mvvm.presenter.OpenEyesHomePresenter
import com.fphoenixcorneae.openeyes.mvvm.viewmodel.activity.OpenEyesSearchActivity
import com.fphoenixcorneae.openeyes.mvvm.viewmodel.activity.OpenEyesVideoDetailActivity
import com.fphoenixcorneae.openeyes.mvvm.viewmodel.adapter.wrapper.OpenEyesHomeBannerWrapper
import com.fphoenixcorneae.openeyes.mvvm.viewmodel.adapter.wrapper.OpenEyesHomeDateWrapper
import com.fphoenixcorneae.openeyes.mvvm.viewmodel.adapter.wrapper.OpenEyesHomeVideoWrapper
import com.fphoenixcorneae.titlebar.CommonTitleBar
import com.fphoenixcorneae.util.BundleBuilder
import com.fphoenixcorneae.util.ColorUtil
import com.fphoenixcorneae.util.IntentUtil
import com.fphoenixcorneae.util.ScreenUtil
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import kotlinx.android.synthetic.main.open_eyes_fragment_home.*
import kotlinx.android.synthetic.main.open_eyes_item_home_video.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

/**
 * @desc 首页-精选Fragment
 */
class OpenEyesHomeFragment :
    OpenEyesBaseDagger2Fragment<OpenEyesHomeContract.View, OpenEyesHomePresenter>(),
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
        initSmartRefreshLayout()
        initRecyclerView()
    }

    private fun initSmartRefreshLayout() {
        mSrlRefresh.apply {
            // 内容跟随偏移
            setEnableHeaderTranslationContent(true)
            // 下拉刷新、上拉加载监听
            setOnRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
                override fun onLoadMore(refreshLayout: RefreshLayout) {
                    mPresenter.loadMoreData()
                }

                override fun onRefresh(refreshLayout: RefreshLayout) {
                    mPresenter.requestHomeData(1)
                }
            })
            // 设置下拉刷新主题颜色
            setPrimaryColorsId(
                R.color.open_eyes_color_bg_default,
                R.color.open_eyes_color_bg_default
            )
            // 打开下拉刷新区域块背景
            mMhHeader.setShowBezierWave(true)
        }
    }

    private fun initRecyclerView() {
        mVideoWrapper.setOnItemClickListener { viewHolder, _, item ->
            // 跳转到视频详情页面
            IntentUtil.startActivity(
                mContext,
                OpenEyesVideoDetailActivity::class.java,
                BundleBuilder.of().putSerializable(
                    OpenEyesConstants.EXTRA_KEY_VIDEO_DATA,
                    item
                ).get(),
                -1,
                viewHolder.itemView.mIvCoverFeed,
                viewHolder.itemView.mIvAvatar
            )
        }
        mVideoWrapper.setOnItemLongClickListener { viewHolder, position, item ->

            true
        }
        // 注册多类型布局
        mAdapter.register(
            OpenEyesHomeBannerWrapper(),
            OpenEyesHomeDateWrapper(),
            mVideoWrapper,
            delegation = object : Delegation<OpenEyesHomeBean.Issue.Item> {
                override fun getWrapperType(item: OpenEyesHomeBean.Issue.Item): Class<out ViewHolderWrapper<OpenEyesHomeBean.Issue.Item>> {
                    return when (item.type) {
                        "textHeader" -> OpenEyesHomeDateWrapper::class.java
                        "video" -> OpenEyesHomeVideoWrapper::class.java
                        else -> OpenEyesHomeBannerWrapper::class.java
                    }
                }
            })
        mRvRecycler.apply {
            adapter = mAdapter
            layoutManager = mLinearLayoutManager
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                /**
                 * RecyclerView滚动的时候调用
                 */
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    when (val currentVisibleItemPosition =
                        mLinearLayoutManager.findFirstVisibleItemPosition()) {
                        0 -> {
                            mTbTitleBar.centerTextView?.text =
                                getString(R.string.open_eyes_home_choiceness)
                            // 根据索引来获取对应的itemView
                            val firstVisibleChildView: View? =
                                mLinearLayoutManager.findViewByPosition(currentVisibleItemPosition)
                            // 获取当前item偏移量
                            val firstVisibleChildViewTop: Int = firstVisibleChildView?.top ?: 0
                            val firstVisibleChildViewHeight =
                                firstVisibleChildView?.measuredHeight ?: ScreenUtil.screenHeight / 3
                            // 设置标题栏背景透明度
                            if (abs(firstVisibleChildViewTop) <= firstVisibleChildViewHeight) {
                                val alpha: Int =
                                    ((1f - abs(firstVisibleChildViewTop).toFloat() / firstVisibleChildViewHeight) * 255).toInt()
                                if (alpha > 255 || alpha < 80) {
                                    return
                                }
                                mTbTitleBar.setBackgroundColor(
                                    ColorUtil.setAlphaComponent(Color.WHITE, alpha)
                                )
                            }
                        }
                        else -> {
                            // 设置标题栏背景透明度
                            mTbTitleBar.setBackgroundColor(
                                ColorUtil.setAlphaComponent(Color.WHITE, 80)
                            )
                            if (mAdapter.data.size > 1 && currentVisibleItemPosition < mAdapter.data.size) {
                                val itemList = mAdapter.data
                                val item =
                                    itemList[currentVisibleItemPosition] as OpenEyesHomeBean.Issue.Item
                                val title: String?
                                title = when (item.type) {
                                    "textHeader" -> item.data?.text
                                    else -> mSimpleDateFormat.format(item.data?.date)
                                }
                                if (!TextUtils.equals(mTbTitleBar.centerTextView?.text, title)) {
                                    mTbTitleBar.centerTextView?.text = title
                                }
                            }
                        }
                    }
                }
            })
        }
    }

    override fun initListener() {
        mTbTitleBar.init { v, action, _ ->
            if (action == CommonTitleBar.MotionAction.ACTION_RIGHT_BUTTON) {
                v.transitionName = getString(R.string.titlebar_search_hint)
                IntentUtil.startActivity(
                    mContext,
                    OpenEyesSearchActivity::class.java,
                    views = arrayOf(v)
                )
            }
        }
    }

    override fun lazyLoadData() {
        showLoading()
        mTbTitleBar.gone()
        mSrlRefresh.autoRefresh()
    }

    override fun isAlreadyLoadedData(): Boolean {
        return !mAdapter.data.isNullOrEmpty()
    }

    override fun setHomeData(homeBean: OpenEyesHomeBean) {
        showContent()
        mTbTitleBar.visible()
        mTbTitleBar.centerTextView?.text = getString(R.string.open_eyes_home_choiceness)
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
        mSrlRefresh.finishLoadMore()
        mAdapter.data.addAll(itemList)
        mAdapter.notifyDataSetChanged()
    }

    override fun showErrorMsg(t: Throwable) {
        mSrlRefresh.finishRefresh()
        mSrlRefresh.finishLoadMore()
        super.showErrorMsg(t)
    }
}
