package com.wkz.adapter.swipehelper

import android.graphics.Canvas
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ItemTouchHelper

class PlusItemSlideCallback(private val slidTranslate: SlidTranslate?) : WItemTouchHelperPlus.Callback() {

    override val isItemViewSwipeEnabled: Boolean
        get() = true


    internal override val slideViewWidth: Int
        get() = 0

    override val itemSlideType: String
        get() = WItemTouchHelperPlus.SLIDE_ITEM_TYPE_ITEMVIEW

    override fun getMovementFlags(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder): Int {
        return WItemTouchHelperPlus.Callback.makeMovementFlags(0, ItemTouchHelper.START)
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

    }

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        var dX = dX
        if (viewHolder is SwipeBaseHolder) {
            val actionWidth = viewHolder.actionWidth
            if (dX < -actionWidth) {
                dX = -actionWidth
            }
            slidTranslate?.translationX(viewHolder, dX)
        }
    }

    override fun clearView(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
    }
}
