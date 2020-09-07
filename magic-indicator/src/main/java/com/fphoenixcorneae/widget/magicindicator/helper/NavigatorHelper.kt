package com.fphoenixcorneae.widget.magicindicator.helper

import android.util.SparseArray
import android.util.SparseBooleanArray

import com.fphoenixcorneae.widget.magicindicator.ScrollState

/**
 * 方便扩展IPagerNavigator的帮助类，将ViewPager的3个回调方法转换成
 * onSelected、onDeselected、onEnter等回调，方便扩展
 */
class NavigatorHelper {
    private val mDeselectedItems = SparseBooleanArray()
    private val mLeavedPercents = SparseArray<Float>()

    var totalCount: Int = 0
        set(totalCount) {
            field = totalCount
            mDeselectedItems.clear()
            mLeavedPercents.clear()
        }
    var currentIndex: Int = 0
        private set
    private var mLastIndex: Int = 0
    private var mLastPositionOffsetSum: Float = 0.toFloat()
    var scrollState: Int = 0
        private set

    private var mSkimOver: Boolean = false
    private var mNavigatorScrollListener: NavigatorHelper.OnNavigatorScrollListener? = null

    fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        val currentPositionOffsetSum = position + positionOffset
        var leftToRight = false
        if (mLastPositionOffsetSum <= currentPositionOffsetSum) {
            leftToRight = true
        }
        if (scrollState != ScrollState.SCROLL_STATE_IDLE) {
            if (currentPositionOffsetSum == mLastPositionOffsetSum) {
                return
            }
            var nextPosition = position + 1
            var normalDispatch = true
            if (positionOffset == 0.0f) {
                if (leftToRight) {
                    nextPosition = position - 1
                    normalDispatch = false
                }
            }
            for (i in 0 until totalCount) {
                if (i == position || i == nextPosition) {
                    continue
                }
                val leavedPercent = mLeavedPercents.get(i, 0.0f)
                if (leavedPercent != 1.0f) {
                    dispatchOnLeave(i, 1.0f, leftToRight, true)
                }
            }
            if (normalDispatch) {
                if (leftToRight) {
                    dispatchOnLeave(position, positionOffset, true, false)
                    dispatchOnEnter(nextPosition, positionOffset, true, false)
                } else {
                    dispatchOnLeave(nextPosition, 1.0f - positionOffset, false, false)
                    dispatchOnEnter(position, 1.0f - positionOffset, false, false)
                }
            } else {
                dispatchOnLeave(nextPosition, 1.0f - positionOffset, true, false)
                dispatchOnEnter(position, 1.0f - positionOffset, true, false)
            }
        } else {
            for (i in 0 until totalCount) {
                if (i == currentIndex) {
                    continue
                }
                val deselected = mDeselectedItems.get(i)
                if (!deselected) {
                    dispatchOnDeselected(i)
                }
                val leavedPercent = mLeavedPercents.get(i, 0.0f)
                if (leavedPercent != 1.0f) {
                    dispatchOnLeave(i, 1.0f, false, true)
                }
            }
            dispatchOnEnter(currentIndex, 1.0f, false, true)
            dispatchOnSelected(currentIndex)
        }
        mLastPositionOffsetSum = currentPositionOffsetSum
    }

    private fun dispatchOnEnter(index: Int, enterPercent: Float, leftToRight: Boolean, force: Boolean) {
        if (mSkimOver || index == currentIndex || scrollState == ScrollState.SCROLL_STATE_DRAGGING || force) {
            if (mNavigatorScrollListener != null) {
                mNavigatorScrollListener!!.onEnter(index, totalCount, enterPercent, leftToRight)
            }
            mLeavedPercents.put(index, 1.0f - enterPercent)
        }
    }

    private fun dispatchOnLeave(index: Int, leavePercent: Float, leftToRight: Boolean, force: Boolean) {
        val leaveEnable = (mSkimOver
                || index == mLastIndex
                || scrollState == ScrollState.SCROLL_STATE_DRAGGING
                || (index == currentIndex - 1 || index == currentIndex + 1) && mLeavedPercents.get(index, 0.0f) != 1.0f
                || force)
        if (leaveEnable) {
            if (mNavigatorScrollListener != null) {
                mNavigatorScrollListener!!.onLeave(index, totalCount, leavePercent, leftToRight)
            }
            mLeavedPercents.put(index, leavePercent)
        }
    }

    private fun dispatchOnSelected(index: Int) {
        if (mNavigatorScrollListener != null) {
            mNavigatorScrollListener!!.onSelected(index, totalCount)
        }
        mDeselectedItems.put(index, false)
    }

    private fun dispatchOnDeselected(index: Int) {
        if (mNavigatorScrollListener != null) {
            mNavigatorScrollListener!!.onDeselected(index, totalCount)
        }
        mDeselectedItems.put(index, true)
    }

    fun onPageSelected(position: Int) {
        mLastIndex = currentIndex
        currentIndex = position
        dispatchOnSelected(currentIndex)
        for (i in 0 until totalCount) {
            if (i == currentIndex) {
                continue
            }
            val deselected = mDeselectedItems.get(i)
            if (!deselected) {
                dispatchOnDeselected(i)
            }
        }
    }

    fun onPageScrollStateChanged(state: Int) {
        scrollState = state
    }

    fun setNavigatorScrollListener(navigatorScrollListener: NavigatorHelper.OnNavigatorScrollListener) {
        mNavigatorScrollListener = navigatorScrollListener
    }

    fun setSkimOver(skimOver: Boolean) {
        mSkimOver = skimOver
    }

    interface OnNavigatorScrollListener {
        fun onEnter(index: Int, totalCount: Int, enterPercent: Float, leftToRight: Boolean)

        fun onLeave(index: Int, totalCount: Int, leavePercent: Float, leftToRight: Boolean)

        fun onSelected(index: Int, totalCount: Int)

        fun onDeselected(index: Int, totalCount: Int)
    }
}
