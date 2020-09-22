package com.fphoenixcorneae.framework.widget.recyclerview

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs

/**
 * @desc 滑动停靠左对齐
 * @date 2020-09-22 15:20
 */
class StartSnapHelper : LinearSnapHelper() {
    private var mVerticalHelper: OrientationHelper? = null
    private var mHorizontalHelper: OrientationHelper? = null
    override fun calculateDistanceToFinalSnap(
        layoutManager: RecyclerView.LayoutManager,
        targetView: View
    ): IntArray? {
        return calculateDisOnStart(layoutManager, targetView)
    }

    override fun findSnapView(layoutManager: RecyclerView.LayoutManager): View? {
        return findStartSnapView(layoutManager)
    }

    /**
     * find the start view
     *
     * @param layoutManager
     * @return
     */
    private fun findStartSnapView(layoutManager: RecyclerView.LayoutManager): View? {
        if (layoutManager.canScrollVertically()) {
            return findStartView(layoutManager, getVerticalHelper(layoutManager))
        } else if (layoutManager.canScrollHorizontally()) {
            return findStartView(layoutManager, getHorizontalHelper(layoutManager))
        }
        return null
    }

    private fun calculateDisOnStart(
        layoutManager: RecyclerView.LayoutManager,
        targetView: View
    ): IntArray {
        val out = IntArray(2)
        if (layoutManager.canScrollHorizontally()) {
            out[0] = distanceToStart(
                layoutManager, targetView,
                getHorizontalHelper(layoutManager)
            )
        } else {
            out[0] = 0
        }
        if (layoutManager.canScrollVertically()) {
            out[1] = distanceToStart(
                layoutManager, targetView,
                getVerticalHelper(layoutManager)
            )
        } else {
            out[1] = 0
        }
        return out
    }

    /**
     * calculate distance to start
     *
     * @param layoutManager
     * @param targetView
     * @param helper
     * @return
     */
    private fun distanceToStart(
        layoutManager: RecyclerView.LayoutManager,
        targetView: View, helper: OrientationHelper
    ): Int {
        return helper.getDecoratedStart(targetView) - helper.startAfterPadding
    }

    /**
     * 注意判断最后一个item时，应通过判断距离右侧的位置
     *
     * @param layoutManager
     * @param helper
     * @return
     */
    private fun findStartView(
        layoutManager: RecyclerView.LayoutManager,
        helper: OrientationHelper
    ): View? {
        if (layoutManager !is LinearLayoutManager) {
            // only for LinearLayoutManager
            return null
        }
        val childCount = layoutManager.getChildCount()
        if (childCount == 0) {
            return null
        }
        var closestChild: View? = null
        val start = helper.startAfterPadding
        var absClosest = Int.MAX_VALUE
        for (i in 0 until childCount) {
            val child = layoutManager.getChildAt(i)
            val childStart = helper.getDecoratedStart(child)
            val absDistance = abs(childStart - start)
            if (absDistance < absClosest) {
                absClosest = absDistance
                closestChild = child
            }
        }
        val firstVisibleChild = layoutManager.getChildAt(0)
        if (firstVisibleChild !== closestChild) {
            return closestChild
        }
        val firstChildStart = helper.getDecoratedStart(firstVisibleChild)
        val lastChildPos = layoutManager.findLastVisibleItemPosition()
        val lastChild = layoutManager.getChildAt(childCount - 1)
        val lastChildCenter =
            helper.getDecoratedStart(lastChild) + helper.getDecoratedMeasurement(lastChild) / 2
        val isEndItem = lastChildPos == layoutManager.getItemCount() - 1
        return if (isEndItem && firstChildStart < 0 && lastChildCenter < helper.end) {
            lastChild
        } else closestChild
    }

    private fun getVerticalHelper(layoutManager: RecyclerView.LayoutManager): OrientationHelper {
        if (mVerticalHelper == null) {
            mVerticalHelper = OrientationHelper.createVerticalHelper(layoutManager)
        }
        return mVerticalHelper!!
    }

    private fun getHorizontalHelper(
        layoutManager: RecyclerView.LayoutManager
    ): OrientationHelper {
        if (mHorizontalHelper == null) {
            mHorizontalHelper = OrientationHelper.createHorizontalHelper(layoutManager)
        }
        return mHorizontalHelper!!
    }
}