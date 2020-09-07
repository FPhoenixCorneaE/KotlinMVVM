package com.fphoenixcorneae.widget.magicindicator.titleview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout

import com.fphoenixcorneae.widget.magicindicator.IMeasurablePagerTitleView

/**
 * 通用的指示器标题，子元素内容由外部提供，事件回传给外部
 */
class CommonPagerTitleView(context: Context) : FrameLayout(context), IMeasurablePagerTitleView {
    var onPagerTitleChangeListener: OnPagerTitleChangeListener? = null
    var contentPositionDataProvider: ContentPositionDataProvider? = null

    override val contentLeft: Int
        get() = if (contentPositionDataProvider != null) {
            contentPositionDataProvider!!.contentLeft
        } else left

    override val contentTop: Int
        get() = if (contentPositionDataProvider != null) {
            contentPositionDataProvider!!.contentTop
        } else top

    override val contentRight: Int
        get() = if (contentPositionDataProvider != null) {
            contentPositionDataProvider!!.contentRight
        } else right

    override val contentBottom: Int
        get() = if (contentPositionDataProvider != null) {
            contentPositionDataProvider!!.contentBottom
        } else bottom

    override fun onSelected(index: Int, totalCount: Int) {
        if (onPagerTitleChangeListener != null) {
            onPagerTitleChangeListener!!.onSelected(index, totalCount)
        }
    }

    override fun onDeselected(index: Int, totalCount: Int) {
        if (onPagerTitleChangeListener != null) {
            onPagerTitleChangeListener!!.onDeselected(index, totalCount)
        }
    }

    override fun onLeave(index: Int, totalCount: Int, leavePercent: Float, leftToRight: Boolean) {
        if (onPagerTitleChangeListener != null) {
            onPagerTitleChangeListener!!.onLeave(index, totalCount, leavePercent, leftToRight)
        }
    }

    override fun onEnter(index: Int, totalCount: Int, enterPercent: Float, leftToRight: Boolean) {
        if (onPagerTitleChangeListener != null) {
            onPagerTitleChangeListener!!.onEnter(index, totalCount, enterPercent, leftToRight)
        }
    }

    @JvmOverloads
    fun setContentView(contentView: View?, lp: FrameLayout.LayoutParams? = null) {
        var lp = lp
        removeAllViews()
        if (contentView != null) {
            if (lp == null) {
                lp = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            }
            addView(contentView, lp)
        }
    }

    fun setContentView(layoutId: Int) {
        val child = LayoutInflater.from(context).inflate(layoutId, null)
        setContentView(child, null)
    }

    interface OnPagerTitleChangeListener {
        fun onSelected(index: Int, totalCount: Int)

        fun onDeselected(index: Int, totalCount: Int)

        fun onLeave(index: Int, totalCount: Int, leavePercent: Float, leftToRight: Boolean)

        fun onEnter(index: Int, totalCount: Int, enterPercent: Float, leftToRight: Boolean)
    }

    interface ContentPositionDataProvider {
        val contentLeft: Int

        val contentTop: Int

        val contentRight: Int

        val contentBottom: Int
    }
}
/**
 * 外部直接将布局设置进来
 *
 * @param contentView
 */
