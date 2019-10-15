package com.wkz.adapter.wrapper

import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.*
import com.wkz.adapter.bean.LoadMoreBean
import kotlin.math.max

/**
 * 加载更多封装
 *
 * @author zhouchunjie
 * @date 2017/10/9
 */
abstract class BaseLoadMoreWrapper(@LayoutRes private val layoutId: Int = -1) : ViewHolderWrapper<LoadMoreBean>(layoutId) {

    private var recyclerView: RecyclerView? = null

    val loadMoreBean = LoadMoreBean()
    var onLoadMoreListener: (() -> Unit)? = null

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (checkCanLoadMore(recyclerView, dx, dy)) startLoadMore()
            }
        })
    }

    override fun getSpanSize(item: LoadMoreBean): Int = Integer.MAX_VALUE

    /**
     * 判断是否可以加载更多
     *
     * @param recyclerView
     */
    private fun checkCanLoadMore(recyclerView: RecyclerView, dx: Int, dy: Int): Boolean {
        // 判断是否有加载更多项
        val hasLoadMoreBean = multiTypeAdapter.data.indexOf(loadMoreBean) != -1
        if (!hasLoadMoreBean || loadMoreBean.loadState == LoadMoreBean.STATUS_LOADING) return false

        val layoutManager = recyclerView.layoutManager
        val lastVisibleItemPosition: Int
        val orientation: Int
        when (layoutManager) {
            is GridLayoutManager -> {
                lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                orientation = layoutManager.orientation
            }
            is LinearLayoutManager -> {
                lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                orientation = layoutManager.orientation
            }
            is StaggeredGridLayoutManager -> {
                val lastPositions = layoutManager.findLastVisibleItemPositions(IntArray(layoutManager.spanCount))
                lastVisibleItemPosition = findMax(lastPositions)
                orientation = layoutManager.orientation
            }
            else -> {
                throw IllegalArgumentException("Unsupported LayoutManager used. Valid ones are LinearLayoutManager, GridLayoutManager and StaggeredGridLayoutManager.")
            }
        }
        return lastVisibleItemPosition >= multiTypeAdapter.itemCount - 1
                && (orientation == OrientationHelper.VERTICAL && dy > 0 || orientation == OrientationHelper.HORIZONTAL && dx > 0)
    }

    private fun findMax(positions: IntArray): Int {
        var max = positions[0]
        for (value in positions) {
            max = max(value, max)
        }
        return max
    }

    /**
     * 刷新加载更多布局
     */
    private fun refreshLoadMoreView() {
        val loadMoreIndex = multiTypeAdapter.data.indexOf(loadMoreBean)
        if (loadMoreIndex >= 0) {
            multiTypeAdapter.notifyItemChanged(loadMoreIndex)
        }
    }

    /**
     * 开始加载
     */
    fun startLoadMore() {
        loadMoreBean.loadState = LoadMoreBean.STATUS_LOADING
        recyclerView?.let {
            if (onLoadMoreListener != null) {
                it.post {
                    refreshLoadMoreView()
                    onLoadMoreListener?.invoke()
                }
            }
        }
    }

    /**
     * 是否正在加载
     */
    fun isLoading(): Boolean = loadMoreBean.loadState == LoadMoreBean.STATUS_LOADING

    /**
     * 加载完成
     */
    fun loadCompleted() {
        loadMoreBean.loadState = LoadMoreBean.STATUS_COMPLETED
        refreshLoadMoreView()
    }

    /**
     * 没有更多数据
     */
    fun loadNoMore() {
        loadMoreBean.loadState = LoadMoreBean.STATUS_NO_MORE
        refreshLoadMoreView()
    }

    /**
     * 加载失败
     */
    fun loadFailure() {
        loadMoreBean.loadState = LoadMoreBean.STATUS_FAILURE
        refreshLoadMoreView()
    }
}
