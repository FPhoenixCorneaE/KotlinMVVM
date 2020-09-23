package com.fphoenixcorneae.kotlinmvvm.mvvm.viewmodel.fragment

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fphoenixcorneae.decoration.SpacesItemDecoration
import com.fphoenixcorneae.dp2px
import com.fphoenixcorneae.ext.isNonNullAndNotEmpty
import com.fphoenixcorneae.kotlinmvvm.R
import com.fphoenixcorneae.kotlinmvvm.constant.OpenEyesConstants
import com.fphoenixcorneae.kotlinmvvm.mvvm.contract.OpenEyesCategoryContract
import com.fphoenixcorneae.kotlinmvvm.mvvm.model.bean.OpenEyesCategoryBean
import com.fphoenixcorneae.kotlinmvvm.mvvm.presenter.OpenEyesCategoryPresenter
import com.fphoenixcorneae.kotlinmvvm.mvvm.viewmodel.adapter.OpenEyesCategoryAdapter
import com.fphoenixcorneae.util.ScreenUtil
import kotlinx.android.synthetic.main.open_eyes_fragment_category.*
import kotlin.math.abs

/**
 * @desc 分类Fragment
 * @date 2020-09-22 16:29
 */
class OpenEyesCategoryFragment :
    OpenEyesBaseDagger2Fragment<OpenEyesCategoryContract.View, OpenEyesCategoryPresenter>(),
    OpenEyesCategoryContract.View {

    private val mCategoryAdapter by lazy {
        OpenEyesCategoryAdapter(mContext, arrayListOf())
    }

    override fun getLayoutId(): Int = R.layout.open_eyes_fragment_category

    override fun initView() {
        initRecyclerView()
    }

    private fun initRecyclerView() {
        mRvCategory.apply {
            layoutManager = GridLayoutManager(context, 2)
            setHasFixedSize(true)
            adapter = mCategoryAdapter
            // 分割线
            addItemDecoration(
                SpacesItemDecoration(
                    dp2px(16f),
                    dp2px(16f),
                    ContextCompat.getColor(context, R.color.open_eyes_color_translucent)
                )
            )
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

    }

    override fun lazyLoadData() {
        mPresenter.getCategoryData()
    }

    override fun isAlreadyLoadedData(): Boolean = mCategoryAdapter.mData.isNonNullAndNotEmpty()

    override fun showCategory(categoryList: ArrayList<OpenEyesCategoryBean>) {
        mCategoryAdapter.setData(categoryList)
    }
}