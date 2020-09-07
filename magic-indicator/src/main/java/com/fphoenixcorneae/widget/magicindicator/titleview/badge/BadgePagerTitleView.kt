package com.fphoenixcorneae.widget.magicindicator.titleview.badge

import android.content.Context
import android.view.View
import android.widget.FrameLayout

import com.fphoenixcorneae.widget.magicindicator.IMeasurablePagerTitleView
import com.fphoenixcorneae.widget.magicindicator.IPagerTitleView

/**
 * 支持显示角标的title，角标布局可自定义
 */
class BadgePagerTitleView(context: Context) : FrameLayout(context), IMeasurablePagerTitleView {
    var innerPagerTitleView: IPagerTitleView? = null
        set(innerPagerTitleView) {
            if (this.innerPagerTitleView === innerPagerTitleView) {
                return
            }
            field = innerPagerTitleView
            removeAllViews()
            if (this.innerPagerTitleView is View) {
                val lp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
                addView(this.innerPagerTitleView as View?, lp)
            }
            if (badgeView != null) {
                val lp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT)
                addView(badgeView, lp)
            }
        }
    var badgeView: View? = null
        set(badgeView) {
            if (this.badgeView === badgeView) {
                return
            }
            field = badgeView
            removeAllViews()
            if (innerPagerTitleView is View) {
                val lp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
                addView(innerPagerTitleView as View?, lp)
            }
            if (this.badgeView != null) {
                val lp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT)
                addView(this.badgeView, lp)
            }
        }
    var isAutoCancelBadge = true

    var xBadgeRule: BadgeRule? = null
        set(badgeRule) {
            if (badgeRule != null) {
                val anchor = badgeRule.anchor
                kotlin.require(!(anchor != BadgeAnchor.LEFT
                        && anchor != BadgeAnchor.RIGHT
                        && anchor != BadgeAnchor.CONTENT_LEFT
                        && anchor != BadgeAnchor.CONTENT_RIGHT
                        && anchor != BadgeAnchor.CENTER_X
                        && anchor != BadgeAnchor.LEFT_EDGE_CENTER_X
                        && anchor != BadgeAnchor.RIGHT_EDGE_CENTER_X)) { "x badge rule is wrong." }
            }
            field = badgeRule
        }
    var yBadgeRule: BadgeRule? = null
        set(badgeRule) {
            if (badgeRule != null) {
                val anchor = badgeRule.anchor
                kotlin.require(!(anchor != BadgeAnchor.TOP
                        && anchor != BadgeAnchor.BOTTOM
                        && anchor != BadgeAnchor.CONTENT_TOP
                        && anchor != BadgeAnchor.CONTENT_BOTTOM
                        && anchor != BadgeAnchor.CENTER_Y
                        && anchor != BadgeAnchor.TOP_EDGE_CENTER_Y
                        && anchor != BadgeAnchor.BOTTOM_EDGE_CENTER_Y)) { "y badge rule is wrong." }
            }
            field = badgeRule
        }

    override val contentLeft: Int
        get() = if (innerPagerTitleView is IMeasurablePagerTitleView) {
            left + (innerPagerTitleView as IMeasurablePagerTitleView).contentLeft
        } else left

    override val contentTop: Int
        get() = if (innerPagerTitleView is IMeasurablePagerTitleView) {
            (innerPagerTitleView as IMeasurablePagerTitleView).contentTop
        } else top

    override val contentRight: Int
        get() = if (innerPagerTitleView is IMeasurablePagerTitleView) {
            left + (innerPagerTitleView as IMeasurablePagerTitleView).contentRight
        } else right

    override val contentBottom: Int
        get() = if (innerPagerTitleView is IMeasurablePagerTitleView) {
            (innerPagerTitleView as IMeasurablePagerTitleView).contentBottom
        } else bottom

    override fun onSelected(index: Int, totalCount: Int) {
        if (innerPagerTitleView != null) {
            innerPagerTitleView!!.onSelected(index, totalCount)
        }
        if (isAutoCancelBadge) {
            badgeView = null
        }
    }

    override fun onDeselected(index: Int, totalCount: Int) {
        if (innerPagerTitleView != null) {
            innerPagerTitleView!!.onDeselected(index, totalCount)
        }
    }

    override fun onLeave(index: Int, totalCount: Int, leavePercent: Float, leftToRight: Boolean) {
        if (innerPagerTitleView != null) {
            innerPagerTitleView!!.onLeave(index, totalCount, leavePercent, leftToRight)
        }
    }

    override fun onEnter(index: Int, totalCount: Int, enterPercent: Float, leftToRight: Boolean) {
        if (innerPagerTitleView != null) {
            innerPagerTitleView!!.onEnter(index, totalCount, enterPercent, leftToRight)
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (innerPagerTitleView is View && badgeView != null) {
            // 14种角标定位方式
            val position = IntArray(14)
            val v = innerPagerTitleView as View?
            position[0] = v!!.left
            position[1] = v.top
            position[2] = v.right
            position[3] = v.bottom
            if (innerPagerTitleView is IMeasurablePagerTitleView) {
                val view = innerPagerTitleView as IMeasurablePagerTitleView?
                position[4] = view!!.contentLeft
                position[5] = view.contentTop
                position[6] = view.contentRight
                position[7] = view.contentBottom
            } else {
                for (i in 4..7) {
                    position[i] = position[i - 4]
                }
            }
            position[8] = v.width / 2
            position[9] = v.height / 2
            position[10] = position[4] / 2
            position[11] = position[5] / 2
            position[12] = position[6] + (position[2] - position[6]) / 2
            position[13] = position[7] + (position[3] - position[7]) / 2

            // 根据设置的BadgeRule调整角标的位置
            if (xBadgeRule != null) {
                val x = position[xBadgeRule!!.anchor!!.ordinal]
                val offset = xBadgeRule!!.offset
                val newLeft = x + offset
                badgeView!!.offsetLeftAndRight(newLeft - badgeView!!.left)
            }
            if (yBadgeRule != null) {
                val y = position[yBadgeRule!!.anchor!!.ordinal]
                val offset = yBadgeRule!!.offset
                val newTop = y + offset
                badgeView!!.offsetTopAndBottom(newTop - badgeView!!.top)
            }
        }
    }
}
