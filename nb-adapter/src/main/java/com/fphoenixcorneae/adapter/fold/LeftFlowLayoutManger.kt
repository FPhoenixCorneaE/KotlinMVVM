package com.fphoenixcorneae.adapter.fold

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Rect
import android.util.DisplayMetrics
import android.util.SparseArray
import android.util.SparseBooleanArray
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator

import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 *
 * 通过重写LayoutManger布局方法[.onLayoutChildren]
 * 对Item进行布局，并对超出屏幕的Item进行回收
 *
 * 通过重写LayoutManger中的[.scrollHorizontallyBy]
 * 进行水平滚动处理
 *
 *
 * 参考https://blog.csdn.net/harvic880925/article/details/86606873
 */
class LeftFlowLayoutManger(private val builder: Builder) : RecyclerView.LayoutManager() {

    /**
     * 屏幕的宽度
     */
    private val screenWidth: Int
        get() {
            val windowManager =
                builder.context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val dm = DisplayMetrics()
            windowManager.defaultDisplay?.getMetrics(dm)
            return dm.widthPixels
        }

    /**
     * 最大存储item信息存储数量，
     * 超过设置数量，则动态计算来获取
     */
    private val MAX_RECT_COUNT = 100

    /**
     * 滑动总偏移量
     */
    private var mOffsetAll = 0

    /**
     * Item宽
     */
    private var mDecoratedChildWidth = 0

    /**
     * Item高
     */
    private var mDecoratedChildHeight = 0

    /**
     * Item间隔与item宽的比例
     */
    private val mIntervalRatio = builder.cstIntervalRatio

    /**
     * 保存所有的Item的上下左右的偏移量信息
     */
    private val mAllItemFrames = SparseArray<Rect>()

    /**
     * 记录Item是否出现过屏幕且还没有回收。true表示出现过屏幕上，并且还没被回收
     */
    private val mHasAttachedItems = SparseBooleanArray()

    /**
     * RecyclerView的Item回收器
     */
    private var mRecycle: RecyclerView.Recycler? = null

    /**
     * RecyclerView的状态器
     */
    private var mState: RecyclerView.State? = null

    /**
     * 滚动动画
     */
    private var mAnimation: ValueAnimator? = null

    /**
     * 正显示在中间的Item
     */
    /**
     * 获取被选中Item位置
     */
    var selectedPos = 0
        private set

    /**
     * 前一个正显示在中间的Item
     */
    private var mLastSelectPosition = 0

    /**
     * 选中监听
     */
    private var mSelectedListener: OnSelected? = null

    /**
     * 屏幕中最多显示完整item的个数  即最后不能滑动的item个数
     */
    private var endNum: Int = 0
    private var isStay: Boolean = false

    /**
     * 获取整个布局的水平空间大小
     */
    private val horizontalSpace: Int
        get() = width - paddingRight - paddingLeft

    /**
     * 获取整个布局的垂直空间大小
     */
    private val verticalSpace: Int
        get() = height - paddingBottom - paddingTop

    /**
     * 获取最大偏移量
     */
    private val maxOffset: Float
        get() = (itemCount - endNum) * intervalDistance

    /**
     * 获取Item间隔
     */
    private val intervalDistance: Float
        get() = mDecoratedChildWidth * mIntervalRatio

    /**
     * 获取第一个可见的Item位置
     *
     * Note:该Item为绘制在可见区域的第一个Item，有可能被第二个Item遮挡
     */
    val firstVisiblePosition: Int
        get() {
            val displayFrame = Rect(mOffsetAll, 0, mOffsetAll + horizontalSpace, verticalSpace)
            val cur = centerPosition
            for (i in cur - 1 downTo 0) {
                val rect = getFrame(i)
                if (!Rect.intersects(displayFrame, rect)) {
                    return i + 1
                }
            }
            return 0
        }

    /**
     * 获取中间位置
     * Note:该方法主要用于[FoldRecyclerView.getChildDrawingOrder]判断中间位置
     * 如果需要获取被选中的Item位置，调用[.getSelectedPos]
     */
    val centerPosition: Int
        get() {
            var pos = (mOffsetAll / intervalDistance).toInt()
            val more = (mOffsetAll % intervalDistance).toInt()
            if (more > intervalDistance * 0.5f) {
                pos++
            }
            return pos
        }

    init {
        if (mDecoratedChildWidth != 0) {
            endNum = when {
                isStay -> screenWidth / mDecoratedChildWidth
                else -> 1
            }
        }
    }

    fun stayEnd(isStay: Boolean) {
        this.isStay = isStay
        if (!isStay) {
            endNum = 1
        }
    }

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        //如果没有item，直接返回
        //跳过preLayout，preLayout主要用于支持动画
        if (itemCount == 0) {
            mOffsetAll = 0
            detachAndScrapAttachedViews(recycler!!)
            return
        }
        if (itemCount <= 0 && state!!.isPreLayout) {
            mOffsetAll = 0
            return
        }
        detachAndScrapAttachedViews(recycler!!) //在布局之前，将所有的子View先Detach掉，放入到Scrap缓存中
        mAllItemFrames.clear()
        mHasAttachedItems.clear()

        //得到子view的宽和高，这边的item的宽高都是一样的，所以只需要进行一次测量
        val scrap = recycler.getViewForPosition(0)
        addView(scrap)
        measureChildWithMargins(scrap, 0, 0)
        //计算测量布局的宽高
        mDecoratedChildWidth = getDecoratedMeasuredWidth(scrap)
        mDecoratedChildHeight = getDecoratedMeasuredHeight(scrap)
        if (mDecoratedChildWidth != 0) {
            endNum = when {
                isStay -> screenWidth / mDecoratedChildWidth
                else -> 1
            }
        }
        var offset = 0f

        /**只存[MAX_RECT_COUNT]个item具体位置 */
        var i = 0
        while (i < itemCount + 1 && i < MAX_RECT_COUNT) {
            var frame: Rect? = mAllItemFrames.get(i)
            if (frame == null) {
                frame = Rect()
            }
            frame.set(
                offset.roundToInt(),
                0,
                (offset + mDecoratedChildWidth).roundToInt(),
                mDecoratedChildHeight
            )
            mAllItemFrames.put(i, frame)
            mHasAttachedItems.put(i, false)
            //原始位置累加，否则越后面误差越大
            offset += intervalDistance
            i++
        }

        if ((mRecycle == null || mState == null) && selectedPos != 0) {
            //在为初始化前调用smoothScrollToPosition 或者 scrollToPosition,只会记录位置   所以初始化时需要滚动到对应位置
            mOffsetAll = calculateOffsetForPosition(selectedPos)
            onSelectedCallBack()
        }

        layoutItems(recycler, state, SCROLL_RIGHT)

        mRecycle = recycler
        mState = state
    }

    /**
     * dx : 滑动的距离   >0向左    <0向右
     */
    override
    fun scrollHorizontallyBy(
        dx: Int, recycler: RecyclerView.Recycler?,
        state: RecyclerView.State?
    ): Int {
        if (mAnimation != null && mAnimation!!.isRunning) {
            mAnimation!!.cancel()
        }
        var travel = dx
        if (dx + mOffsetAll < 0) {
            //如果滑动到第一个条目则不再继续滑动   滑动到最后一个条目则也不再继续
            travel = -mOffsetAll
        } else if (dx + mOffsetAll > maxOffset) {
            travel = (maxOffset - mOffsetAll).toInt()
        }

        //累计偏移量
        mOffsetAll += travel
        layoutItems(recycler, state, if (dx > 0) SCROLL_RIGHT else SCROLL_LEFT)
        return travel
    }

    /**
     * 布局Item
     *
     * 注意：1，先清除已经超出屏幕的item
     *      2，再绘制可以显示在屏幕里面的item
     */
    private fun layoutItems(
        recycler: RecyclerView.Recycler?,
        state: RecyclerView.State?, scrollDirection: Int
    ) {
        if (state == null || state.isPreLayout) {
            return
        }
        val displayFrame = Rect(mOffsetAll, 0, mOffsetAll + horizontalSpace, verticalSpace)
        var position = 0
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            child?.let {
                position = getPosition(child)
                val rect = getFrame(position)
                if (!Rect.intersects(displayFrame, rect)) {//Item没有在显示区域，就说明需要回收
                    removeAndRecycleView(child, recycler!!) //回收滑出屏幕的View
                    mHasAttachedItems.delete(position)
                } else { //Item还在显示区域内，更新滑动后Item的位置
                    layoutItem(child, rect, position) //更新Item位置
                    mHasAttachedItems.put(position, true)
                }
            }
        }
        if (position == 0) {
            position = selectedPos
        }
        val min = if (position - 50 >= 0) position - 50 else 0
        val max = if (position + 50 < itemCount) position + 50 else itemCount
        for (i in min until max) {
            val rect = getFrame(i)
            if (Rect.intersects(displayFrame, rect) && !mHasAttachedItems.get(i)) { //重新加载可见范围内的Item
                val scrap = recycler!!.getViewForPosition(i)
                measureChildWithMargins(scrap, 0, 0)
                if (scrollDirection == SCROLL_RIGHT) { //向左滚动，新增的Item需要添加在最前面
                    addView(scrap, 0)
                } else { //向右滚动，新增的item要添加在最后面
                    addView(scrap)
                }
                layoutItem(scrap, rect, i) //将这个Item布局出来
                mHasAttachedItems.put(i, true)
            }
        }
    }

    /**
     * 布局Item位置
     *
     * @param child 要布局的Item
     * @param frame 位置信息
     */
    private fun layoutItem(child: View, frame: Rect, pos: Int) {
        if (frame.left - mOffsetAll < 0) {
            val scale = computeScaleX(frame.left - mOffsetAll)
            child.scaleX = scale //缩放
            child.scaleY = scale //缩放
            if (centerPosition != itemCount - 1) {
                child.alpha = computeAlphaX(frame.left - mOffsetAll)
            }
            val left = ((1 - scale) * mDecoratedChildWidth / 2).toInt()
            layoutDecorated(
                child,
                -left,
                frame.top,
                frame.right - frame.left - left,
                frame.bottom
            )
        } else {
            layoutDecorated(
                child,
                frame.left - mOffsetAll,
                frame.top,
                frame.right - mOffsetAll,
                frame.bottom
            )
            //缩放
            child.scaleX = 1f
            //缩放
            child.scaleY = 1f
            child.alpha = 1f
        }
    }

    /**
     * 动态获取Item的位置信息
     *
     * @param index item位置
     * @return item的Rect信息
     */
    private fun getFrame(index: Int): Rect {
        var frame: Rect? = mAllItemFrames.get(index)
        if (frame == null) {
            frame = Rect()
            val offset = intervalDistance * index //原始位置累加（即累计间隔距离）
            frame.set(
                offset.roundToInt(),
                0,
                (offset + mDecoratedChildWidth).roundToInt(),
                mDecoratedChildHeight
            )
        }
        return frame
    }

    override fun onScrollStateChanged(state: Int) {
        super.onScrollStateChanged(state)
        when (state) {
            RecyclerView.SCROLL_STATE_IDLE ->
                //滚动停止时
                fixOffsetWhenFinishScroll()
            RecyclerView.SCROLL_STATE_DRAGGING -> {
                //拖拽滚动时
            }
            RecyclerView.SCROLL_STATE_SETTLING -> {
                //动画滚动时
            }
        }
    }

    override fun scrollToPosition(position: Int) {
        if (position < 0 || position > itemCount - 1) {
            return
        }
        mOffsetAll = calculateOffsetForPosition(position)
        if (mRecycle == null || mState == null) {
            //如果RecyclerView还没初始化完，先记录下要滚动的位置
            selectedPos = position
        } else {
            layoutItems(
                mRecycle,
                mState,
                if (position > selectedPos) SCROLL_RIGHT else SCROLL_LEFT
            )
            onSelectedCallBack()
        }
    }

    override fun smoothScrollToPosition(
        recyclerView: RecyclerView?,
        state: RecyclerView.State?,
        position: Int
    ) {
        val finalOffset = calculateOffsetForPosition(position)
        if (mRecycle == null || mState == null) {//如果RecyclerView还没初始化完，先记录下要滚动的位置
            selectedPos = position
        } else {
            startScroll(mOffsetAll, finalOffset)
        }
    }

    override fun canScrollHorizontally(): Boolean {
        return true
    }

    override fun onAdapterChanged(
        oldAdapter: RecyclerView.Adapter<*>?,
        newAdapter: RecyclerView.Adapter<*>?
    ) {
        removeAllViews()
        mRecycle = null
        mState = null
        mOffsetAll = 0
        selectedPos = 0
        mLastSelectPosition = 0
        mHasAttachedItems.clear()
        mAllItemFrames.clear()
    }

    /**
     * 计算Item缩放系数
     *
     * @param x Item的偏移量
     * @return 缩放系数
     */
    private fun computeScaleX(x: Int): Float {
        var scale = 1 - 0.2f * abs(x) / mDecoratedChildWidth
        if (scale > 1) {
            scale = 1f
        }
        return scale

    }

    private fun computeAlphaX(x: Int): Float {
        var alpha = 1 - 0.8f * abs(x) / mDecoratedChildWidth
        if (alpha > 1) {
            alpha = 1f
        }
        return alpha
    }

    /**
     * 计算Item所在的位置偏移
     *
     * @param position 要计算Item位置
     */
    private fun calculateOffsetForPosition(position: Int): Int {
        return (intervalDistance * position).roundToInt()
    }

    /**
     * 修正停止滚动后，Item滚动到中间位置
     */
    private fun fixOffsetWhenFinishScroll() {
        var scrollN = (mOffsetAll * 1.0f / intervalDistance).toInt()
        val moreDx = mOffsetAll % intervalDistance
        if (moreDx > intervalDistance * 0.5) {
            scrollN++
        }
        val finalOffset = (scrollN * intervalDistance).toInt()
        startScroll(mOffsetAll, finalOffset)
        selectedPos = Math.round(finalOffset * 1.0f / intervalDistance)
    }

    /**
     * 滚动到指定X轴位置
     *
     * @param from X轴方向起始点的偏移量
     * @param to   X轴方向终点的偏移量
     */
    private fun startScroll(from: Int, to: Int) {
        var to = to
        if (to > 0) {
            to -= 30
        }
        if (mAnimation != null && mAnimation!!.isRunning) {
            mAnimation!!.cancel()
        }
        val direction = if (from < to) SCROLL_RIGHT else SCROLL_LEFT
        mAnimation = ValueAnimator.ofFloat(from.toFloat(), to.toFloat())
        mAnimation!!.duration = 500
        mAnimation!!.interpolator = DecelerateInterpolator()
        mAnimation!!.addUpdateListener { animation ->
            mOffsetAll = (animation.animatedValue as Float).roundToInt()
            layoutItems(mRecycle, mState, direction)
        }
        mAnimation!!.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                onSelectedCallBack()
            }
        })
        mAnimation!!.start()
    }

    /**
     * 计算当前选中位置，并回调
     */
    private fun onSelectedCallBack() {
        selectedPos = when (intervalDistance) {
            0f -> 0
            else -> (mOffsetAll / intervalDistance).roundToInt()
        }
        if (mSelectedListener != null && selectedPos != mLastSelectPosition) {
            mSelectedListener!!.onItemSelected(selectedPos)
        }
        mLastSelectPosition = selectedPos
    }


    /**
     * 设置选中监听
     *
     * @param l 监听接口
     */
    fun setOnSelectedListener(l: OnSelected) {
        mSelectedListener = l
    }

    /**
     * 选中监听接口
     */
    interface OnSelected {
        /**
         * 监听选中回调
         *
         * @param position 显示在中间的Item的位置
         */
        fun onItemSelected(position: Int)
    }

    class Builder(internal val context: Context) {
        internal var cstIntervalRatio = 1F

        fun setIntervalRatio(ratio: Float): Builder {
            cstIntervalRatio = ratio
            return this
        }

        fun build(): LeftFlowLayoutManger {
            return LeftFlowLayoutManger(this)
        }
    }

    companion object {

        /**
         * 滑动的方向：左
         */
        private const val SCROLL_LEFT = 1

        /**
         * 滑动的方向：右
         */
        private const val SCROLL_RIGHT = 2
    }
}