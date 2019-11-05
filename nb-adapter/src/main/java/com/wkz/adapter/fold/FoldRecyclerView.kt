package com.wkz.adapter.fold

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs

/**
 * @desc: 折叠RecyclerView
 * 继承RecyclerView重写[.getChildDrawingOrder]对Item的绘制顺序进行控制
 * 参考https://blog.csdn.net/harvic880925/article/details/86606873
 */
class FoldRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : RecyclerView(context, attrs, defStyle) {
    /**
     * 按下的X轴坐标
     */
    private var mDownX: Float = 0.toFloat()

    /**
     * 布局器构建者
     */
    private var mManagerBuilder: LeftFlowLayoutManger.Builder? = null

    /**
     * 获取LayoutManger，并强制转换为 LeftFlowLayoutManger
     */
    val mLayoutManager: LeftFlowLayoutManger
        get() = layoutManager as LeftFlowLayoutManger

    /**
     * 获取被选中的Item位置
     */
    val selectedPos: Int
        get() = mLayoutManager.selectedPos

    init {
        init()
    }

    private fun init() {
        createManageBuilder()
        layoutManager = mManagerBuilder!!.build()
        //开启重新排序
        isChildrenDrawingOrderEnabled = true
        overScrollMode = View.OVER_SCROLL_NEVER
    }

    override fun generateDefaultLayoutParams(): ViewGroup.LayoutParams {
        return LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    /**
     * 创建布局构建器
     */
    private fun createManageBuilder() {
        if (mManagerBuilder == null) {
            mManagerBuilder = LeftFlowLayoutManger.Builder(context)
        }
    }

    fun stayEnd(isStay: Boolean) {
        mLayoutManager.stayEnd(isStay)
    }

    /**
     * 设置Item的间隔比例
     *
     * @param intervalRatio Item间隔比例。
     * 即：item的宽 x intervalRatio
     */
    fun setIntervalRatio(intervalRatio: Float) {
        createManageBuilder()
        mManagerBuilder!!.setIntervalRatio(intervalRatio)
        layoutManager = mManagerBuilder!!.build()
    }

    override fun setLayoutManager(layout: LayoutManager?) {
        require(layout is LeftFlowLayoutManger) { "The layout manager must be LeftFlowLayoutManger" }
        super.setLayoutManager(layout)
    }


    public override fun getChildDrawingOrder(childCount: Int, i: Int): Int {
        return abs(childCount - 1 - i)
    }

    /**
     * 设置选中监听
     *
     * @param l 监听接口
     */
    fun setOnItemSelectedListener(l: LeftFlowLayoutManger.OnSelected) {
        mLayoutManager.setOnSelectedListener(l)
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                mDownX = ev.x
                //设置父类不拦截滑动事件
                parent.requestDisallowInterceptTouchEvent(true)
            }
            MotionEvent.ACTION_MOVE -> when {
                (ev.x > mDownX && mLayoutManager.centerPosition == 0)
                        || (ev.x < mDownX && mLayoutManager.centerPosition == mLayoutManager.itemCount - 1) ->
                    //如果是滑动到了最前和最后，开放父类滑动事件拦截
                    parent.requestDisallowInterceptTouchEvent(false)
                else ->
                    //滑动到中间，设置父类不拦截滑动事件
                    parent.requestDisallowInterceptTouchEvent(true)
            }
        }
        return super.dispatchTouchEvent(ev)
    }
}
