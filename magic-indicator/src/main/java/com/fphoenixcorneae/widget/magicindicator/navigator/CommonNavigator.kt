package com.fphoenixcorneae.widget.magicindicator.navigator

import android.content.Context
import android.database.DataSetObserver
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import com.fphoenixcorneae.widget.magicindicator.*
import com.fphoenixcorneae.widget.magicindicator.adapter.CommonNavigatorAdapter
import com.fphoenixcorneae.widget.magicindicator.helper.NavigatorHelper
import com.fphoenixcorneae.widget.magicindicator.model.PositionData
import java.util.*
import kotlin.math.min

/**
 * 通用的ViewPager指示器，包含PagerTitle和PagerIndicator
 */
class CommonNavigator(context: Context) : FrameLayout(context), IPagerNavigator,
    NavigatorHelper.OnNavigatorScrollListener {
    private var mScrollView: HorizontalScrollView? = null
    var titleContainer: LinearLayout? = null
        private set
    private var mIndicatorContainer: LinearLayout? = null
    var pagerIndicator: IPagerIndicator? = null
        private set

    // adapter改变时，应该重新init，但是第一次设置adapter不用，onAttachToMagicIndicator中有init
    var adapter: CommonNavigatorAdapter? = null
        set(adapter) {
            if (this.adapter === adapter) {
                return
            }
            if (this.adapter != null) {
                this.adapter!!.unregisterDataSetObserver(mObserver)
            }
            field = adapter
            if (this.adapter != null) {
                this.adapter!!.registerDataSetObserver(mObserver)
                mNavigatorHelper.totalCount = this.adapter!!.count
                if (titleContainer != null) {
                    this.adapter!!.notifyDataSetChanged()
                }
            } else {
                mNavigatorHelper.totalCount = 0
                init()
            }
        }
    private val mNavigatorHelper: NavigatorHelper = NavigatorHelper()

    /**
     * 提供给外部的参数配置
     */
    //==============================================================================================
    /**
     * 自适应模式，适用于数目固定的、少量的title
     */
    var isAdjustMode: Boolean = false
    /**
     * 启动中心点滚动
     */
    var isEnablePivotScroll: Boolean = false
    /**
     * 滚动中心点 0.0f - 1.0f
     */
    var scrollPivotX = 0.5f
    /**
     * 是否平滑滚动，适用于 !mAdjustMode && !mFollowTouch
     */
    var isSmoothScroll = true
    /**
     * 是否手指跟随滚动
     */
    var isFollowTouch = true
    var rightPadding: Int = 0
    var leftPadding: Int = 0
    /**
     * 指示器是否在title上层，默认为下层
     */
    var isIndicatorOnTop: Boolean = false
    /**
     * 跨多页切换时，中间页是否显示 "掠过" 效果
     */
    var isSkimOver: Boolean = false
        set(skimOver) {
            field = skimOver
            mNavigatorHelper.setSkimOver(skimOver)
        }
    /**
     * PositionData准备好时，是否重新选中当前页，为true可保证在极端情况下指示器状态正确
     */
    var isReselectWhenLayout = true
    //==============================================================================================

    /**
     * 保存每个title的位置信息，为扩展indicator提供保障
     */
    private val mPositionDataList = ArrayList<PositionData>()

    private val mObserver = object : DataSetObserver() {

        override fun onChanged() {
            // 如果使用helper，应始终保证helper中的totalCount为最新
            mNavigatorHelper.totalCount = adapter!!.count
            init()
        }

        override fun onInvalidated() {
            // 没什么用，暂不做处理
        }
    }

    init {
        mNavigatorHelper.setNavigatorScrollListener(this)
    }

    override fun notifyDataSetChanged() {
        if (adapter != null) {
            adapter!!.notifyDataSetChanged()
        }
    }

    private fun init() {
        removeAllViews()

        val root: View
        if (isAdjustMode) {
            root = LayoutInflater.from(context)
                .inflate(R.layout.pager_navigator_layout_no_scroll, this)
        } else {
            root = LayoutInflater.from(context).inflate(R.layout.pager_navigator_layout, this)
            // mAdjustMode为true时，mScrollView为null
            mScrollView = root.findViewById<View>(R.id.scroll_view) as HorizontalScrollView
        }

        titleContainer = root.findViewById<View>(R.id.title_container) as LinearLayout
        titleContainer!!.setPadding(leftPadding, 0, rightPadding, 0)

        mIndicatorContainer = root.findViewById<View>(R.id.indicator_container) as LinearLayout
        if (isIndicatorOnTop) {
            mIndicatorContainer!!.parent.bringChildToFront(mIndicatorContainer)
        }

        initTitlesAndIndicator()
    }

    /**
     * 初始化title和indicator
     */
    private fun initTitlesAndIndicator() {
        var i = 0
        val j = mNavigatorHelper.totalCount
        while (i < j) {
            val v = adapter!!.getTitleView(context, i)
            if (v is View) {
                val view = v as View
                val lp: LinearLayout.LayoutParams
                if (isAdjustMode) {
                    lp = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT)
                    lp.weight = adapter!!.getTitleWeight(context, i)
                } else {
                    lp = LinearLayout.LayoutParams(
                        LayoutParams.WRAP_CONTENT,
                        LayoutParams.MATCH_PARENT
                    )
                }
                titleContainer!!.addView(view, lp)
            }
            i++
        }
        if (adapter != null) {
            pagerIndicator = adapter!!.getIndicator(context)
            if (pagerIndicator is View) {
                val lp = LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                mIndicatorContainer!!.addView(pagerIndicator as View?, lp)
            }
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (adapter != null) {
            preparePositionData()
            if (pagerIndicator != null) {
                pagerIndicator!!.onPositionDataProvide(mPositionDataList)
            }
            if (isReselectWhenLayout && mNavigatorHelper.scrollState == ScrollState.SCROLL_STATE_IDLE) {
                onPageSelected(mNavigatorHelper.currentIndex)
                onPageScrolled(mNavigatorHelper.currentIndex, 0.0f, 0)
            }
        }
    }

    /**
     * 获取title的位置信息，为打造不同的指示器、各种效果提供可能
     */
    private fun preparePositionData() {
        mPositionDataList.clear()
        var i = 0
        val j = mNavigatorHelper.totalCount
        while (i < j) {
            val data = PositionData()
            val v = titleContainer!!.getChildAt(i)
            if (v != null) {
                data.mLeft = v.left
                data.mTop = v.top
                data.mRight = v.right
                data.mBottom = v.bottom
                if (v is IMeasurablePagerTitleView) {
                    val view = v as IMeasurablePagerTitleView
                    data.mContentLeft = view.contentLeft
                    data.mContentTop = view.contentTop
                    data.mContentRight = view.contentRight
                    data.mContentBottom = view.contentBottom
                } else {
                    data.mContentLeft = data.mLeft
                    data.mContentTop = data.mTop
                    data.mContentRight = data.mRight
                    data.mContentBottom = data.mBottom
                }
            }
            mPositionDataList.add(data)
            i++
        }
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        if (adapter != null) {

            mNavigatorHelper.onPageScrolled(position, positionOffset, positionOffsetPixels)
            if (pagerIndicator != null) {
                pagerIndicator!!.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            // 手指跟随滚动
            if (mScrollView != null && mPositionDataList.size > 0 && position >= 0 && position < mPositionDataList.size) {
                if (isFollowTouch) {
                    val currentPosition = min(mPositionDataList.size - 1, position)
                    val nextPosition = min(mPositionDataList.size - 1, position + 1)
                    val current = mPositionDataList[currentPosition]
                    val next = mPositionDataList[nextPosition]
                    val scrollTo = current.horizontalCenter() - mScrollView!!.width * scrollPivotX
                    val nextScrollTo = next.horizontalCenter() - mScrollView!!.width * scrollPivotX
                    mScrollView!!.scrollTo(
                        (scrollTo + (nextScrollTo - scrollTo) * positionOffset).toInt(),
                        0
                    )
                } else if (!isEnablePivotScroll) {
                    // TODO 实现待选中项完全显示出来
                }
            }
        }
    }

    override fun onPageSelected(position: Int) {
        if (adapter != null) {
            mNavigatorHelper.onPageSelected(position)
            if (pagerIndicator != null) {
                pagerIndicator!!.onPageSelected(position)
            }
        }
    }

    override fun onPageScrollStateChanged(state: Int) {
        if (adapter != null) {
            mNavigatorHelper.onPageScrollStateChanged(state)
            if (pagerIndicator != null) {
                pagerIndicator!!.onPageScrollStateChanged(state)
            }
        }
    }

    override fun onAttachToMagicIndicator() {
        // 将初始化延迟到这里
        init()
    }

    override fun onDetachFromMagicIndicator() {}

    override fun onEnter(index: Int, totalCount: Int, enterPercent: Float, leftToRight: Boolean) {
        if (titleContainer == null) {
            return
        }
        val v = titleContainer!!.getChildAt(index)
        if (v is IPagerTitleView) {
            (v as IPagerTitleView).onEnter(index, totalCount, enterPercent, leftToRight)
        }
    }

    override fun onLeave(index: Int, totalCount: Int, leavePercent: Float, leftToRight: Boolean) {
        if (titleContainer == null) {
            return
        }
        val v = titleContainer!!.getChildAt(index)
        if (v is IPagerTitleView) {
            (v as IPagerTitleView).onLeave(index, totalCount, leavePercent, leftToRight)
        }
    }

    override fun onSelected(index: Int, totalCount: Int) {
        if (titleContainer == null) {
            return
        }
        val v = titleContainer!!.getChildAt(index)
        if (v is IPagerTitleView) {
            (v as IPagerTitleView).onSelected(index, totalCount)
        }
        if (!isAdjustMode && !isFollowTouch && mScrollView != null && mPositionDataList.size > 0) {
            val currentIndex = min(mPositionDataList.size - 1, index)
            val current = mPositionDataList[currentIndex]
            if (isEnablePivotScroll) {
                val scrollTo = current.horizontalCenter() - mScrollView!!.width * scrollPivotX
                if (isSmoothScroll) {
                    mScrollView!!.smoothScrollTo(scrollTo.toInt(), 0)
                } else {
                    mScrollView!!.scrollTo(scrollTo.toInt(), 0)
                }
            } else {
                // 如果当前项被部分遮挡，则滚动显示完全
                if (mScrollView!!.scrollX > current.mLeft) {
                    if (isSmoothScroll) {
                        mScrollView!!.smoothScrollTo(current.mLeft, 0)
                    } else {
                        mScrollView!!.scrollTo(current.mLeft, 0)
                    }
                } else if (mScrollView!!.scrollX + width < current.mRight) {
                    if (isSmoothScroll) {
                        mScrollView!!.smoothScrollTo(current.mRight - width, 0)
                    } else {
                        mScrollView!!.scrollTo(current.mRight - width, 0)
                    }
                }
            }
        }
    }

    override fun onDeselected(index: Int, totalCount: Int) {
        if (titleContainer == null) {
            return
        }
        val v = titleContainer!!.getChildAt(index)
        if (v is IPagerTitleView) {
            (v as IPagerTitleView).onDeselected(index, totalCount)
        }
    }

    fun getPagerTitleView(index: Int): IPagerTitleView? {
        return if (titleContainer == null) {
            null
        } else titleContainer!!.getChildAt(index) as IPagerTitleView
    }
}
