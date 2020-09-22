package com.fphoenixcorneae.kotlinmvvm.mvvm.viewmodel.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fphoenixcorneae.ext.isNonNullAndNotEmpty
import com.fphoenixcorneae.framework.base.fragment.Dagger2InjectionFragment
import com.fphoenixcorneae.kotlinmvvm.R
import com.fphoenixcorneae.kotlinmvvm.mvvm.contract.OpenEyesFollowContract
import com.fphoenixcorneae.kotlinmvvm.mvvm.model.bean.OpenEyesHomeBean
import com.fphoenixcorneae.kotlinmvvm.mvvm.presenter.OpenEyesFollowPresenter
import com.fphoenixcorneae.kotlinmvvm.mvvm.viewmodel.adapter.OpenEyesFollowAdapter
import com.fphoenixcorneae.util.ScreenUtil
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import kotlinx.android.synthetic.main.open_eyes_fragment_follow.*
import kotlin.math.abs

/**
 * @desc 发现-关注Fragment
 * @date 2020-09-18 13:42
 */
class OpenEyesFollowFragment :
    Dagger2InjectionFragment<OpenEyesFollowContract.View, OpenEyesFollowPresenter>(),
    OpenEyesFollowContract.View {

    private val mFollowAdapter by lazy {
        OpenEyesFollowAdapter(mContext, arrayListOf())
    }

    override fun getLayoutId(): Int = R.layout.open_eyes_fragment_follow

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
                    mPresenter.requestFollowList()
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
        mRvFollow.apply {
            setHasFixedSize(true)
            adapter = mFollowAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    (recyclerView.layoutManager as? LinearLayoutManager)?.apply {
                        when (val currentVisibleItemPosition =
                            findFirstVisibleItemPosition()) {
                            0 -> {
                                // 根据索引来获取对应的itemView
                                val firstVisibleChildView: View? =
                                    findViewByPosition(
                                        currentVisibleItemPosition
                                    )
                                // 获取当前item偏移量
                                val firstVisibleChildViewTop: Int = firstVisibleChildView?.top ?: 0
                                val firstVisibleChildViewHeight =
                                    firstVisibleChildView?.measuredHeight
                                        ?: ScreenUtil.screenHeight / 3
                                // 设置标题栏背景透明度
                                if (abs(firstVisibleChildViewTop) <= firstVisibleChildViewHeight) {
                                    val alpha: Int =
                                        ((1f - abs(firstVisibleChildViewTop).toFloat() / firstVisibleChildViewHeight) * 255).toInt()
                                    if (alpha > 255 || alpha < 80) {
                                        return
                                    }
                                    // 设置标题栏背景透明度
                                    setFragmentResult("discoveryTitle", Bundle().apply {
                                        putInt("alpha", alpha)
                                    })
                                }
                            }
                            else -> {
                                // 设置标题栏背景透明度
                                setFragmentResult("discoveryTitle", Bundle().apply {
                                    putInt("alpha", 80)
                                })
                            }
                        }
                    }
                }
            })
        }
    }

    override fun initListener() {

    }

    override fun lazyLoadData() {
        showLoading()
        mSrlRefresh.autoRefresh()
    }

    override fun isAlreadyLoadedData(): Boolean = mFollowAdapter.mData.isNonNullAndNotEmpty()

    override fun setFollowInfo(issue: OpenEyesHomeBean.Issue) {
        showContent()
        mSrlRefresh.finishRefresh()
        mFollowAdapter.setData(issue.itemList)
    }

    override fun loadMoreFollowInfo(issue: OpenEyesHomeBean.Issue) {
        mSrlRefresh.finishLoadMore()
        mFollowAdapter.addAllData(issue.itemList)
    }

    override fun showErrorMsg(t: Throwable) {
        mSrlRefresh.finishRefresh()
        mSrlRefresh.finishLoadMore()
        super.showErrorMsg(t)
    }
}