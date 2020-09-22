package com.fphoenixcorneae.framework.widget.recyclerview

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView

/**
 * @desc 滑动停靠右对齐
 * @date 2020-09-22 15:23
 */
class EndSnapHelper : LinearSnapHelper() {
    private var mVerticalHelper: OrientationHelper? = null
    private var mHorizontalHelper: OrientationHelper? = null
    override fun calculateDistanceToFinalSnap(
        layoutManager: RecyclerView.LayoutManager,
        targetView: View
    ): IntArray? {
        return calculateDisOnEnd(layoutManager, targetView)
    }

    override fun findSnapView(layoutManager: RecyclerView.LayoutManager): View? {
        return findEndSnapView(layoutManager)
    }

    private fun calculateDisOnEnd(
        layoutManager: RecyclerView.LayoutManager,
        targetView: View
    ): IntArray {
        val out = IntArray(2)
        if (layoutManager.canScrollHorizontally()) {
            out[0] = distanceToEnd(
                layoutManager, targetView,
                getHorizontalHelper(layoutManager)
            )
        } else {
            out[0] = 0
        }
        if (layoutManager.canScrollVertically()) {
            out[1] = distanceToEnd(
                layoutManager, targetView,
                getVerticalHelper(layoutManager)
            )
        } else {
            out[1] = 0
        }
        return out
    }

    /**
     * calculate distance to end
     *
     * @param layoutManager
     * @param targetView
     * @param helper
     * @return
     */
    private fun distanceToEnd(
        layoutManager: RecyclerView.LayoutManager,
        targetView: View, helper: OrientationHelper
    ): Int {
        return helper.getDecoratedEnd(targetView) - helper.endAfterPadding
    }

    /**
     * find the end view
     *
     * @param layoutManager
     * @return
     */
    private fun findEndSnapView(layoutManager: RecyclerView.LayoutManager): View? {
        if (layoutManager.canScrollVertically()) {
            return findEndView(layoutManager, getVerticalHelper(layoutManager))
        } else if (layoutManager.canScrollHorizontally()) {
            return findEndView(layoutManager, getHorizontalHelper(layoutManager))
        }
        return null
    }

    private fun findEndView(
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
        if (layoutManager.findLastCompletelyVisibleItemPosition() == 0) {
            return null
        }
        var closestChild: View? = null
        val end = helper.endAfterPadding
        var absClosest = Int.MAX_VALUE
        for (i in 0 until childCount) {
            val child = layoutManager.getChildAt(i)
            val childStart = helper.getDecoratedEnd(child)
            val absDistance = Math.abs(childStart - end)
            if (absDistance < absClosest) {
                absClosest = absDistance
                closestChild = child
            }
        }
        val lastVisibleChild = layoutManager.getChildAt(childCount - 1)
        if (lastVisibleChild !== closestChild) {
            return closestChild
        }
        if (closestChild != null
            && layoutManager.getPosition(closestChild) == layoutManager.findLastCompletelyVisibleItemPosition()
        ) {
            return closestChild
        }
        val firstChild = layoutManager.getChildAt(0)
        val firstChildStart = helper.getDecoratedStart(firstChild)
        val firstChildPos = layoutManager.findFirstVisibleItemPosition()
        val isFirstItem = firstChildPos == 0
        val firstChildCenter =
            helper.getDecoratedStart(firstChild) + helper.getDecoratedMeasurement(firstChild) / 2
        return if (isFirstItem && firstChildStart < 0 && firstChildCenter > helper.startAfterPadding) {
            firstChild
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