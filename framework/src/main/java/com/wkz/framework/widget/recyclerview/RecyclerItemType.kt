package com.wkz.framework.widget.recyclerview

/**
 * @desc: 多布局条目类型
 */
interface RecyclerItemType<in T> {
    fun getLayoutId(item: T, position: Int): Int
}
