package com.wkz.framework.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import me.drakeet.multitype.ItemViewBinder

/**
 * @desc: 多布局RecyclerView的ItemViewBinder基类
 */
abstract class BaseItemViewBinder<T : IViewBinder> : ItemViewBinder<T, BaseItemViewBinder.ViewHolder>() {

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return ViewHolder(DataBindingUtil.inflate(inflater, getLayoutId(), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, data: T) {
        setData(holder, data)
    }

    abstract fun getLayoutId(): Int

    abstract fun setData(holder: ViewHolder, data: T)


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}