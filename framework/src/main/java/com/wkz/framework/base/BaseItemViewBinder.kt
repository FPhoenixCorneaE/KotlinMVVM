package com.wkz.framework.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import me.drakeet.multitype.ItemViewBinder

/**
 * @desc: 多布局RecyclerView的ItemViewBinder基类
 */
abstract class BaseItemViewBinder<T : IViewBinder, DB : ViewDataBinding> :
    ItemViewBinder<T, BaseItemViewBinder.ViewHolder>() {

    protected lateinit var mBinding: DB

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        val itemView = inflater.inflate(getLayoutId(), parent, false)
        mBinding = DataBindingUtil.bind(itemView)!!
        // 迫使数据立即绑定而不是在下一帧的时候才绑定
        // 假设没使用executePendingBindings()方法，由于在下一帧的时候才会绑定，
        // view就会绑定错误的data，测量也会出错。
        // 因此，executePendingBindings()是很重要的。
        mBinding.executePendingBindings()
        return ViewHolder(mBinding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, data: T) {
        setData(holder, data)
    }

    abstract fun getLayoutId(): Int

    abstract fun setData(holder: ViewHolder, data: T)


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}