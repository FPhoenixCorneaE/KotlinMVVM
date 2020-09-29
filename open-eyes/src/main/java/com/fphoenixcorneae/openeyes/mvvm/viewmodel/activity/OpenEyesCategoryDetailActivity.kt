package com.fphoenixcorneae.openeyes.mvvm.viewmodel.activity

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fphoenixcorneae.framework.base.activity.Dagger2InjectionActivity
import com.fphoenixcorneae.framework.glide.GlideUtil
import com.fphoenixcorneae.openeyes.R
import com.fphoenixcorneae.openeyes.constant.OpenEyesConstants
import com.fphoenixcorneae.openeyes.mvvm.contract.OpenEyesCategoryDetailContract
import com.fphoenixcorneae.openeyes.mvvm.model.bean.OpenEyesCategoryBean
import com.fphoenixcorneae.openeyes.mvvm.model.bean.OpenEyesHomeBean
import com.fphoenixcorneae.openeyes.mvvm.presenter.OpenEyesCategoryDetailPresenter
import com.fphoenixcorneae.openeyes.mvvm.viewmodel.adapter.OpenEyesCategoryDetailAdapter
import com.fphoenixcorneae.util.ColorUtil
import com.fphoenixcorneae.util.ScreenUtil
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import kotlinx.android.synthetic.main.open_eyes_activity_category_detail.*
import kotlin.math.abs

/**
 * @desc 分类详情 Activity
 */
class OpenEyesCategoryDetailActivity :
    Dagger2InjectionActivity<OpenEyesCategoryDetailContract.View, OpenEyesCategoryDetailPresenter>(),
    OpenEyesCategoryDetailContract.View, OnLoadMoreListener {

    private val mCategoryDetailAdapter by lazy {
        OpenEyesCategoryDetailAdapter(mContext, arrayListOf())
    }

    override fun getLayoutId(): Int = R.layout.open_eyes_activity_category_detail

    override fun initView() {
        mTbTitleBar.setBackgroundColor(Color.TRANSPARENT)
        initSmartRefreshLayout()
        initRecyclerView()
    }

    override fun initData(savedInstanceState: Bundle?) {
        intent.getParcelableExtra<OpenEyesCategoryBean>(OpenEyesConstants.EXTRA_KEY_CATEGORY_DATA)
            ?.apply {
                // category name
                mTbTitleBar.centerTextView?.text = name
                // headerImage
                GlideUtil.setupImage(
                    mIvCategoryHeader,
                    headerImage,
                    ColorDrawable(ColorUtil.randomColor)
                )
                // description
                mTvCategoryDesc.text = description

                showLoading()
                mPresenter.getCategoryDetailList(id)
            }
    }

    private fun initSmartRefreshLayout() {
        mSrlRefresh.apply {
            // 不允许刷新
            setEnableRefresh(false)
            setOnLoadMoreListener(this@OpenEyesCategoryDetailActivity)
        }
    }

    private fun initRecyclerView() {
        mRvCategoryDetail.apply {
            setHasFixedSize(true)
            adapter = mCategoryDetailAdapter
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
                            }
                        }
                    }
                }
            })
        }
    }

    override fun setCateDetailList(itemList: ArrayList<OpenEyesHomeBean.Issue.Item>) {
        showContent()
        mSrlRefresh.finishLoadMore()
        mCategoryDetailAdapter.addAllData(itemList)
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        mPresenter.loadMoreData()
    }
}