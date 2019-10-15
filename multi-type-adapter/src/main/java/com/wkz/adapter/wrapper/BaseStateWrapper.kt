package com.wkz.adapter.wrapper

import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.wkz.adapter.bean.StateBean

/**
 * 状态封装
 */
abstract class BaseStateWrapper(@LayoutRes private val layoutId: Int = -1) : ViewHolderWrapper<StateBean>(layoutId) {

    private var recyclerView: RecyclerView? = null
    private val stateBean = StateBean()

    override fun getSpanSize(item: StateBean): Int = Integer.MAX_VALUE

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    /**
     * 更新状态
     *
     * @param state
     */
    fun setState(state: Int) {
        stateBean.state = state
        recyclerView?.let {
            multiTypeAdapter.data.clear()
            multiTypeAdapter.data.add(stateBean)
            multiTypeAdapter.notifyDataSetChanged()
            val layoutManager = it.layoutManager
            if (layoutManager != null) {
                if (layoutManager is StaggeredGridLayoutManager) {
                    layoutManager.invalidateSpanAssignments()
                }
                layoutManager.scrollToPosition(0)
            }
        }
    }
}
