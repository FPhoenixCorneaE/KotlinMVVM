package com.wkz.adapter

import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import kotlin.math.abs

/**
 * @desc: 万能RecyclerView适配器
 */
abstract class BaseNBAdapter<T> : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    //正常type 0;
    internal val TYPE_NORMAL = 0
    //头部type默认小于-100 > -10000  我相信头部不能能回家到9900个，如果超过这个数把底部继续拉下限
    internal val TYPE_HEAD = -100
    //底部type默认小于-10000
    internal val TYPE_FOOT = -10000

    //头部储存器
    internal var headViews = ArrayList<View>()
    //底部储存器
    internal var footViews = ArrayList<View>()
    //数据源
    var dataList: ArrayList<T> = ArrayList()


    /*
     * 下列参数和设置动画相关
     * */
    //记录时间差，看看是否大于50，防止动画一起执行；
    private var currentMillions = 0
    //记录已经启动过动画的position的位置
    private var currentPosition = -1
    //如果一直存在小于50的时候，用于叠加delay时间
    private var delayTimePosition = 1
    private var animationType: AnimationType? = null
    private var animResId: Int = 0
    private var alwaysShow: Boolean = false

    var onItemClickListener: OnItemClickListener<T>? = null


    var onItemLongClickListener: OnItemLongClickListener<T>? = null

    /**
     * 多类型布局可重写此方法以及[.getViewHolder()]
     */
    open fun getMyViewType(position: Int): Int {
        return 0
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            headViews.size > 0 -> {
                when {
                    footViews.size > 0 -> {
                        //有头部，也有底部
                        when {
                            position <= headViews.size - 1 -> TYPE_HEAD - position
                            headViews.size - 1 < position && position < headViews.size + dataList.size -> getMyViewType(
                                getRealPosition(position)
                            )
                            else -> TYPE_FOOT - position + dataList.size + headViews.size
                        }
                    }
                    else -> {
                        //有头部，没有底部
                        when {
                            position <= headViews.size - 1 -> TYPE_HEAD - position
                            else -> getMyViewType(getRealPosition(position))
                        }
                    }
                }
            }
            else -> {
                when {
                    footViews.size > 0 -> {
                        //没有头部，有底部的时候
                        when {
                            position < dataList.size -> getMyViewType(position)
                            else -> TYPE_FOOT - position + dataList.size
                        }
                    }
                    else -> {
                        //没有头部，也没有底部的时候
                        getMyViewType(position)
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int = dataList.size + headViews.size + footViews.size

    fun getResId(viewGroup: ViewGroup): View {
        return LayoutInflater.from(viewGroup.context).inflate(getLayoutId(), viewGroup, false)
    }

    fun getViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return NBViewHolder.create(getResId(viewGroup))
    }

    @LayoutRes
    abstract fun getLayoutId(): Int

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType <= TYPE_FOOT) {
            HeaderFooterHolder(footViews[abs(viewType + abs(TYPE_FOOT))])
        } else if (TYPE_FOOT < viewType && viewType <= TYPE_HEAD) {
            HeaderFooterHolder(headViews[abs(viewType + abs(TYPE_HEAD))])
        } else {
            getViewHolder(viewGroup, viewType)
        }
    }

    /**
     * 绑定数据
     */
    abstract fun onBindData(viewHolder: RecyclerView.ViewHolder, data: T, position: Int)

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        //这里已经包括了TYPE_FOOT
        if (getItemViewType(position) <= TYPE_HEAD) {
            return
        }

        if (onItemClickListener != null) {
            viewHolder.itemView.setOnClickListener {
                onItemClickListener!!.onItemClick(
                    dataList[getRealPosition(
                        position
                    )], getRealPosition(position)
                )
            }
        }

        if (onItemLongClickListener != null) {
            viewHolder.itemView.setOnLongClickListener {
                onItemLongClickListener!!.onItemLongClick(
                    dataList[getRealPosition(position)],
                    getRealPosition(position)
                )
                true
            }
        }

        // 绑定数据
        onBindData(
            viewHolder,
            dataList[getRealPosition(position)],
            getRealPosition(position)
        )
        addItemAnimation(viewHolder, getRealPosition(position))
    }

    private fun addItemAnimation(viewHolder: RecyclerView.ViewHolder, position: Int) {
        if (animResId != 0 || animationType != null) {
            if (!alwaysShow) {
                if (position > currentPosition) {
                    currentPosition = position
                    val nowMillis = System.currentTimeMillis().toInt()
                    var animator: Animation? = null
                    if (animResId != 0) {
                        animator =
                            AnimationUtils.loadAnimation(viewHolder.itemView.context, animResId)
                    } else {
                        if (animationType != null) {
                            animator = AnimationUtils.loadAnimation(
                                viewHolder.itemView.context,
                                animationType!!.resId
                            )
                        }
                    }
                    //这里处理的是当前页显示的item执行动画的间隔太快，看不出效果
                    if (nowMillis - currentMillions >= 10) {
                        delayTimePosition = 1
                        currentMillions = nowMillis
                        viewHolder.itemView.startAnimation(animator)
                    } else {
                        delayTimePosition++
                        currentMillions = nowMillis
                        if (animator != null) {
                            animator.startOffset = (50 * delayTimePosition).toLong()
                            viewHolder.itemView.startAnimation(animator)
                        }
                    }
                }
            } else {
                val nowMillis = System.currentTimeMillis().toInt()
                var animator: Animation? = null
                if (animResId != 0) {
                    animator = AnimationUtils.loadAnimation(viewHolder.itemView.context, animResId)
                } else {
                    if (animationType != null) {
                        animator = AnimationUtils.loadAnimation(
                            viewHolder.itemView.context,
                            animationType!!.resId
                        )
                    }
                }
                if (nowMillis - currentMillions >= 10) {
                    delayTimePosition = 1
                    currentMillions = nowMillis
                    viewHolder.itemView.startAnimation(animator)
                } else {
                    delayTimePosition++
                    currentMillions = nowMillis
                    if (animator != null) {
                        animator.startOffset = (50 * delayTimePosition).toLong()
                        viewHolder.itemView.startAnimation(animator)
                    }
                }
            }

        }
    }


    fun notifyAnimItemPosition() {
        currentPosition = -1
    }

    private fun getRealPosition(position: Int): Int {
        return position - headViews.size
    }

    interface OnItemClickListener<T> {
        fun onItemClick(item: T, position: Int)
    }

    interface OnItemLongClickListener<T> {
        fun onItemLongClick(item: T, position: Int)
    }

    fun addHeadView(view: View) {
        headViews.add(view)
        notifyDataSetChanged()
    }

    fun removeHeadView(view: View) {
        headViews.remove(view)
        notifyDataSetChanged()
    }

    fun removeHeadView(index: Int) {
        if (index < headViews.size) {
            headViews.removeAt(index)
            notifyDataSetChanged()
        }
    }

    fun addFootView(view: View) {
        footViews.add(view)
        notifyDataSetChanged()
    }

    fun removeFootView(view: View) {
        footViews.remove(view)
        notifyDataSetChanged()
    }

    fun removeFootView(index: Int) {
        if (index < footViews.size) {
            footViews.removeAt(index)
            notifyDataSetChanged()
        }
    }

    fun isHeadView(position: Int): Boolean {
        return if (headViews.size > 0) {
            position < headViews.size
        } else {
            false
        }
    }

    fun isFootView(position: Int): Boolean {
        return when {
            footViews.size > 0 -> {
                val dataSize: Int = when (dataList) {
                    null -> 0
                    else -> dataList.size
                }
                val totalSize = headViews.size + dataSize + footViews.size
                totalSize - (position + 1) < footViews.size
            }
            else -> false
        }
    }


    /**
     * 瀑布流添加头部满屏操作
     * @param holder
     */
    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)
        val lp = holder.itemView.layoutParams
        if (lp != null && lp is StaggeredGridLayoutManager.LayoutParams
            && holder is HeaderFooterHolder
        ) {
            lp.isFullSpan = true
        }
    }

    /**
     * 这里是对GridLayoutManager添加头部满屏操作
     * @param recyclerView
     */
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        when {
            recyclerView.layoutManager is GridLayoutManager -> {
                val gridLayoutManager = recyclerView.layoutManager as GridLayoutManager?
                gridLayoutManager!!.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return when {
                            isHeadView(position) || isFootView(position) -> gridLayoutManager.spanCount
                            else -> 1
                        }
                    }
                }
            }
            recyclerView.layoutManager is StaggeredGridLayoutManager -> {
                //这里是防止瀑布流自带动画，引起子view乱跳。第一行出现空白格
                recyclerView.animation = null
                val staggeredGridLayoutManager =
                    recyclerView.layoutManager as StaggeredGridLayoutManager?
                staggeredGridLayoutManager!!.gapStrategy =
                    StaggeredGridLayoutManager.GAP_HANDLING_NONE
            }
        }
    }


    /**
     * 这里是设置网格布局间隔
     * @param recyclerView
     * @param divideDimen
     */
    fun setGridDivide(recyclerView: RecyclerView, divideDimen: Int) {
        when {
            recyclerView.layoutManager is GridLayoutManager -> recyclerView.addItemDecoration(object :
                RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    val padTop = recyclerView.paddingTop
                    val padBottom = recyclerView.paddingBottom
                    val padLeft = recyclerView.paddingLeft
                    val padRight = recyclerView.paddingRight
                    //为上下均等
                    when {
                        footViews.size <= 0 -> {
                            recyclerView.clipToPadding = false
                            when (padBottom) {
                                0 -> recyclerView.setPadding(padLeft, padTop, padRight, divideDimen)
                            }
                        }
                        else -> when (padBottom) {
                            divideDimen -> recyclerView.setPadding(padLeft, padTop, padRight, 0)
                        }
                    }

                    //获取列数
                    val spanCount = (recyclerView.layoutManager as GridLayoutManager).spanCount
                    val position =
                        (view.layoutParams as RecyclerView.LayoutParams).viewAdapterPosition
                    when {
                        isFootView(position) -> {
                            val dataSize: Int = when (dataList) {
                                null -> 0
                                else -> dataList.size
                            }
                            val totalSize = headViews.size + dataSize + footViews.size

                            when {
                                totalSize - position == footViews.size -> outRect.set(
                                    0,
                                    divideDimen,
                                    0,
                                    0
                                )
                            }

                        }
                        else -> when {
                            !isHeadView(position) ->
                                //由于这里函数太复杂，越多列越是不同。
                                //希望高数学的好的朋友出出注意
                                //这里只给大家判断到 5列的情况了
                                when (spanCount) {
                                    2 -> when {
                                        getRealPosition(position) % 2 == 0 -> outRect.set(
                                            divideDimen,
                                            divideDimen,
                                            divideDimen / 2,
                                            0
                                        )
                                        else -> outRect.set(
                                            divideDimen / 2,
                                            divideDimen,
                                            divideDimen,
                                            0
                                        )
                                    }
                                    3 -> when {
                                        getRealPosition(position) % 3 == 0 -> outRect.set(
                                            divideDimen,
                                            divideDimen,
                                            divideDimen / 3,
                                            0
                                        )
                                        getRealPosition(position) % 3 == 1 -> outRect.set(
                                            divideDimen * 2 / 3,
                                            divideDimen,
                                            divideDimen * 2 / 3,
                                            0
                                        )
                                        else -> outRect.set(
                                            divideDimen / 3,
                                            divideDimen,
                                            divideDimen,
                                            0
                                        )
                                    }
                                    4 -> when {
                                        getRealPosition(position) % 4 == 0 -> outRect.set(
                                            divideDimen,
                                            divideDimen,
                                            divideDimen / 4,
                                            0
                                        )
                                        getRealPosition(position) % 4 == 1 -> outRect.set(
                                            divideDimen * 3 / 4,
                                            divideDimen,
                                            divideDimen / 2,
                                            0
                                        )
                                        getRealPosition(position) % 4 == 2 -> outRect.set(
                                            divideDimen / 2,
                                            divideDimen,
                                            divideDimen * 3 / 4,
                                            0
                                        )
                                        else -> outRect.set(
                                            divideDimen / 4,
                                            divideDimen,
                                            divideDimen,
                                            0
                                        )
                                    }
                                    5 -> when {
                                        getRealPosition(position) % 5 == 0 -> outRect.set(
                                            divideDimen,
                                            divideDimen,
                                            divideDimen / 5,
                                            0
                                        )
                                        getRealPosition(position) % 5 == 1 -> outRect.set(
                                            divideDimen * 4 / 5,
                                            divideDimen,
                                            divideDimen * 2 / 5,
                                            0
                                        )
                                        getRealPosition(position) % 5 == 2 -> outRect.set(
                                            divideDimen * 3 / 5,
                                            divideDimen,
                                            divideDimen * 3 / 5,
                                            0
                                        )
                                        getRealPosition(position) % 5 == 3 -> outRect.set(
                                            divideDimen * 2 / 5,
                                            divideDimen,
                                            divideDimen * 4 / 5,
                                            0
                                        )
                                        else -> outRect.set(
                                            divideDimen / 5,
                                            divideDimen,
                                            divideDimen,
                                            0
                                        )
                                    }
                                    else -> outRect.set(divideDimen, divideDimen, divideDimen, 0)
                                }
                        }
                    }

                }
            })
            recyclerView.layoutManager is StaggeredGridLayoutManager -> recyclerView.addItemDecoration(
                object : RecyclerView.ItemDecoration() {
                    override fun getItemOffsets(
                        outRect: Rect,
                        view: View,
                        parent: RecyclerView,
                        state: RecyclerView.State
                    ) {
                        val padTop = recyclerView.paddingTop
                        val padBottom = recyclerView.paddingBottom
                        val padLeft = recyclerView.paddingLeft
                        val padRight = recyclerView.paddingRight
                        //为上下均等
                        when {
                            footViews.size <= 0 -> {
                                recyclerView.clipToPadding = false
                                when (padBottom) {
                                    0 -> recyclerView.setPadding(
                                        padLeft,
                                        padTop,
                                        padRight,
                                        divideDimen
                                    )
                                }
                            }
                            else -> when (padBottom) {
                                divideDimen -> recyclerView.setPadding(padLeft, padTop, padRight, 0)
                            }
                        }
                        val position =
                            (view.layoutParams as RecyclerView.LayoutParams).viewAdapterPosition
                        when {
                            isFootView(position) -> {
                                val dataSize: Int = when (dataList) {
                                    null -> 0
                                    else -> dataList.size
                                }
                                val totalSize = headViews.size + dataSize + footViews.size

                                when {
                                    totalSize - position == footViews.size -> outRect.set(
                                        0,
                                        divideDimen,
                                        0,
                                        0
                                    )
                                }
                            }
                            else -> when {
                                !isHeadView(position) -> outRect.set(
                                    divideDimen / 2,
                                    divideDimen,
                                    divideDimen / 2,
                                    0
                                )
                            }
                        }
                    }
                })
        }
    }


    fun showItemAnim(animationType: AnimationType) {
        this.animationType = animationType
    }

    fun showItemAnim(animationType: AnimationType, alwaysShow: Boolean) {
        this.animationType = animationType
        this.alwaysShow = alwaysShow
    }

    fun showItemAnim(animResId: Int) {
        this.animResId = animResId
    }

    fun showItemAnim(animResId: Int, alwaysShow: Boolean) {
        this.animResId = animResId
        this.alwaysShow = alwaysShow
    }
}
