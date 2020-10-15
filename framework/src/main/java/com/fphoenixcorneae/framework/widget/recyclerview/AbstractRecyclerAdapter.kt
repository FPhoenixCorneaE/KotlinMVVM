package com.fphoenixcorneae.framework.widget.recyclerview

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * @desc: 通用的 Adapter
 */
abstract class AbstractRecyclerAdapter<T>(
    protected var mContext: Context,
    var mData: ArrayList<T>,
    private var mLayoutId: Int
) : RecyclerView.Adapter<ViewHolder>() {
    private var mInflater: LayoutInflater? = null
    private var mTypeSupport: RecyclerItemType<T>? = null

    /**
     * 使用接口回调点击事件
     */
    private var mItemClickListener: ((ViewHolder, T, Int) -> Unit)? = null

    /**
     * 使用接口回调长按事件
     */
    private var mItemLongClickListener: ((ViewHolder, T, Int) -> Boolean)? = null

    init {
        mInflater = LayoutInflater.from(mContext)
    }

    //需要多布局
    constructor(context: Context, data: ArrayList<T>, typeSupport: RecyclerItemType<T>) : this(
        context,
        data,
        -1
    ) {
        this.mTypeSupport = typeSupport
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (mTypeSupport != null) {
            //需要多布局
            mLayoutId = viewType
        }
        //创建view
        val view = mInflater?.inflate(mLayoutId, parent, false)
        return ViewHolder(view!!)
    }

    override fun getItemViewType(position: Int): Int {
        //多布局问题
        return mTypeSupport?.getLayoutId(mData[position], position) ?: super.getItemViewType(
            position
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //绑定数据
        bindData(holder, mData[position], position)

        //条目点击事件
        mItemClickListener?.let {
            holder.itemView.setOnClickListener {
                mItemClickListener!!.invoke(
                    holder,
                    mData[position],
                    position
                )
            }
        }

        //长按点击事件
        mItemLongClickListener?.let {
            holder.itemView.setOnLongClickListener {
                mItemLongClickListener!!.invoke(
                    holder,
                    mData[position],
                    position
                )
            }
        }
    }

    /**
     * 将必要参数传递出去
     *
     * @param holder
     * @param data
     * @param position
     */
    protected abstract fun bindData(holder: ViewHolder, data: T, position: Int)

    override fun getItemCount(): Int {
        return mData.size
    }

    /**
     * 设置数据
     */
    fun setData(datas: ArrayList<T>) {
        mData.clear()
        addAllData(datas)
    }

    /**
     * 添加数据
     */
    fun addData(data: T) {
        mData.add(data)
        notifyDataSetChanged()
    }

    /**
     * 添加数据
     */
    fun addAllData(datas: ArrayList<T>) {
        mData.addAll(datas)
        notifyDataSetChanged()
    }

    /**
     * 清空数据
     */
    fun clear() {
        mData.clear()
        notifyDataSetChanged()
    }

    /**
     * Adapter条目的点击事件
     */
    fun setOnItemClickListener(itemClickListener: ((ViewHolder, T, Int) -> Unit)?) {
        this.mItemClickListener = itemClickListener
    }

    /**
     * Adapter条目的长按事件
     */
    fun setOnItemLongClickListener(itemLongClickListener: ((ViewHolder, T, Int) -> Boolean)?) {
        this.mItemLongClickListener = itemLongClickListener
    }
}
