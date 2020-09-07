package com.fphoenixcorneae.wanandroid.mvvm.view.adapter

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import com.fphoenixcorneae.flowlayout.FlowItem
import com.fphoenixcorneae.flowlayout.FlowLayout
import com.fphoenixcorneae.adapter.BaseNBAdapter
import com.fphoenixcorneae.ext.toHtml
import com.fphoenixcorneae.wanandroid.R
import com.fphoenixcorneae.wanandroid.mvvm.model.WanAndroidSystemBean
import kotlinx.android.synthetic.main.wan_android_recycler_item_square_system.view.*

/**
 * @desc: 广场体系适配器
 * @date: 2020-06-20 14:45
 */
class WanAndroidSquareSystemAdapter : BaseNBAdapter<WanAndroidSystemBean>() {

    override fun getLayoutId(): Int = R.layout.wan_android_recycler_item_square_system

    /**
     * 绑定数据
     */
    @SuppressLint("NewApi")
    override fun onBindData(
        viewHolder: RecyclerView.ViewHolder,
        data: WanAndroidSystemBean,
        position: Int
    ) {
        viewHolder.itemView.apply {
            mTvName.text = data.name.toHtml()
            mRvSystemChild.apply {
                mDatas = data.children as ArrayList<in FlowItem>
                mOnItemClickListener = object : FlowLayout.OnItemClickListener {
                    override fun onItemClick(
                        itemName: CharSequence?,
                        position: Int,
                        isSelected: Boolean,
                        selectedData: ArrayList<in FlowItem>
                    ) {
                        this@WanAndroidSquareSystemAdapter.onItemChildClickListener?.onItemChild1Click(
                            null,
                            data,
                            position
                        )
                    }
                }
            }
        }
    }
}