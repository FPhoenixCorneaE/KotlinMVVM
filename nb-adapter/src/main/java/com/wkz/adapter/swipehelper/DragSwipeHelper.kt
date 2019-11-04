package com.wkz.adapter.swipehelper

import android.graphics.Color
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.wkz.adapter.BaseNBAdapter
import java.util.*

class DragSwipeHelper {
    private var helper: ItemTouchHelper? = null
    private var mDragFlags: Int = 0//拖拽
    private var mSwipeFlags: Int = 0//侧滑删除

    //这里不封装到adapter里，是因为有些拖拽，有些人的item不是满条目的。
    //所以放这里，比较好自由扩展，包括这里的viewHolder,可以强转成你的viewHolder，即可对立面的元素操作了
    fun attachDragRecyclerView(recyclerView: RecyclerView, adapter: BaseNBAdapter<*>) {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN//拖拽
        attachRecyclerView(recyclerView, adapter, dragFlags, 0)
    }

    fun attachSwipeRecyclerView(recyclerView: RecyclerView, adapter: BaseNBAdapter<*>) {
        val swipeFlags = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT//侧滑删除
        attachRecyclerView(recyclerView, adapter, 0, swipeFlags)
    }

    fun attachSwipeAndDragRecyclerView(recyclerView: RecyclerView, adapter: BaseNBAdapter<*>) {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN//拖拽
        val swipeFlags = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT//侧滑删除
        attachRecyclerView(recyclerView, adapter, dragFlags, swipeFlags)
    }


    fun attachRecyclerView(recyclerView: RecyclerView, adapter: BaseNBAdapter<*>, dragFlags: Int, swipeFlags: Int) {
        mDragFlags = dragFlags
        mSwipeFlags = swipeFlags
        //为RecycleView绑定触摸事件
        if (helper != null) {
            return
        }
        helper = ItemTouchHelper(object : ItemTouchHelper.Callback() {
            override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
                //首先回调的方法 返回int表示是否监听该方向
                //int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;//拖拽
                //int swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;//侧滑删除
                return makeMovementFlags(mDragFlags, mSwipeFlags)
            }

            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                //滑动事件
                Collections.swap(adapter.dataList, viewHolder.adapterPosition, target.adapterPosition)
                val fromPosition = viewHolder.adapterPosition
                val toPosition = target.adapterPosition
                adapter.notifyItemMoved(viewHolder.adapterPosition, target.adapterPosition)
                if (toPosition > fromPosition) {
                    adapter.notifyItemRangeChanged(fromPosition, Math.abs(toPosition - fromPosition) + 1)
                } else {
                    adapter.notifyItemRangeChanged(toPosition, Math.abs(toPosition - fromPosition) + 1)
                }
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                //侧滑事件
                val removePosition = viewHolder.adapterPosition
                adapter.dataList!!.removeAt(removePosition)
                adapter.notifyItemRemoved(removePosition)
                adapter.notifyItemRangeChanged(removePosition, adapter.itemCount)
            }


            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                //item长按被选中了，选中item颜色变深
                if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
                    viewHolder!!.itemView.setBackgroundColor(Color.LTGRAY)
                }
                super.onSelectedChanged(viewHolder, actionState)
            }

            override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                super.clearView(recyclerView, viewHolder)
                //松手了，将item恢复成白色
                viewHolder.itemView.setBackgroundColor(Color.WHITE)
            }

            override fun isLongPressDragEnabled(): Boolean {
                //是否可拖拽
                return true
            }
        })
        helper!!.attachToRecyclerView(recyclerView)
    }
}
