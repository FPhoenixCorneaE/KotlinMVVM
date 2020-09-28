package com.fphoenixcorneae.openeyes.mvvm.viewmodel.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fphoenixcorneae.ext.isNonNullAndNotEmpty
import com.fphoenixcorneae.openeyes.R
import com.fphoenixcorneae.openeyes.constant.OpenEyesConstants
import com.fphoenixcorneae.openeyes.mvvm.contract.OpenEyesRankContract
import com.fphoenixcorneae.openeyes.mvvm.model.bean.OpenEyesHomeBean
import com.fphoenixcorneae.openeyes.mvvm.presenter.OpenEyesRankPresenter
import com.fphoenixcorneae.openeyes.mvvm.viewmodel.activity.OpenEyesVideoDetailActivity
import com.fphoenixcorneae.openeyes.mvvm.viewmodel.adapter.OpenEyesRankAdapter
import com.fphoenixcorneae.util.BundleBuilder
import com.fphoenixcorneae.util.IntentUtil
import com.fphoenixcorneae.util.ScreenUtil
import kotlinx.android.synthetic.main.open_eyes_fragment_rank.*
import kotlinx.android.synthetic.main.open_eyes_item_rank.view.*
import kotlin.math.abs

/**
 * @desc 排行榜Fragment
 * @date 2020-09-23 13:55
 */
class OpenEyesRankFragment :
    OpenEyesBaseDagger2Fragment<OpenEyesRankContract.View, OpenEyesRankPresenter>(),
    OpenEyesRankContract.View {

    private val mRankAdapter by lazy {
        OpenEyesRankAdapter(mContext, arrayListOf())
    }

    override fun getLayoutId(): Int = R.layout.open_eyes_fragment_rank

    override fun initView() {
        initRecyclerView()
    }

    private fun initRecyclerView() {
        mRvRank.apply {
            setHasFixedSize(true)
            adapter = mRankAdapter
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
                                    setFragmentResult(
                                        OpenEyesConstants.REQUEST_KEY_TITLE_ALPHA,
                                        Bundle().apply {
                                            putInt(OpenEyesConstants.EXTRA_KEY_ALPHA, alpha)
                                        })
                                }
                            }
                            else -> {
                                // 设置标题栏背景透明度
                                setFragmentResult(
                                    OpenEyesConstants.REQUEST_KEY_TITLE_ALPHA,
                                    Bundle().apply {
                                        putInt(OpenEyesConstants.EXTRA_KEY_ALPHA, 80)
                                    })
                            }
                        }
                    }
                }
            })
        }
    }

    override fun initListener() {
        mRankAdapter.setOnItemClickListener { viewHolder, item, positon ->
            // 跳转到视频详情页面
            IntentUtil.startActivity(
                mContext,
                OpenEyesVideoDetailActivity::class.java,
                BundleBuilder.of().putSerializable(
                    OpenEyesConstants.EXTRA_KEY_VIDEO_DATA,
                    item
                ).get(),
                -1,
                viewHolder.itemView.mIvCoverFeed
            )
        }
    }

    override fun lazyLoadData() {
        arguments?.getString(OpenEyesConstants.EXTRA_KEY_API_URL)?.let { apiUrl ->
            mPresenter.requestRankList(apiUrl)
        }
    }

    override fun isAlreadyLoadedData(): Boolean = mRankAdapter.mData.isNonNullAndNotEmpty()

    override fun setRankList(itemList: ArrayList<OpenEyesHomeBean.Issue.Item>) {
        mRankAdapter.setData(itemList)
    }
}