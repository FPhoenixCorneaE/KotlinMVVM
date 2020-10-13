package com.fphoenixcorneae.wanandroid.mvvm.view.adapter

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import com.fphoenixcorneae.flowlayout.FlowItem
import com.fphoenixcorneae.flowlayout.FlowLayout
import com.fphoenixcorneae.viewpager.BaseNBAdapter
import com.fphoenixcorneae.ext.toHtml
import com.fphoenixcorneae.wanandroid.R
import com.fphoenixcorneae.wanandroid.mvvm.model.WanAndroidNavigationBean
import kotlinx.android.synthetic.main.wan_android_recycler_item_square_navigation.view.*

/**
 * @desc: 广场导航适配器
 * @date: 2020-06-22 10:44
 */
class WanAndroidSquareNavigationAdapter : BaseNBAdapter<WanAndroidNavigationBean>() {

    override fun getLayoutId(): Int = R.layout.wan_android_recycler_item_square_navigation

    /**
     * 绑定数据
     */
    @SuppressLint("NewApi")
    override fun onBindData(
        viewHolder: RecyclerView.ViewHolder,
        data: WanAndroidNavigationBean,
        position: Int
    ) {
        viewHolder.itemView.apply {
            mTvName.text = data.name.toHtml()
            mRvNavigationChild.apply {
                mDatas = data.articles as ArrayList<in FlowItem>
                mOnItemClickListener = object : FlowLayout.OnItemClickListener {
                    override fun onItemClick(
                        itemName: CharSequence?,
                        position: Int,
                        isSelected: Boolean,
                        selectedData: ArrayList<in FlowItem>
                    ) {
                        this@WanAndroidSquareNavigationAdapter.onItemChildClickListener?.onItemChild1Click(
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