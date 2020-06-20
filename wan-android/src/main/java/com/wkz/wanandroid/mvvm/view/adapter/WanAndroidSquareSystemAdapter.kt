package com.wkz.wanandroid.mvvm.view.adapter

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import com.wkz.adapter.BaseNBAdapter
import com.wkz.wanandroid.R
import com.wkz.wanandroid.mvvm.model.WanAndroidSystemBean

/**
 * @desc: 广场体系适配器
 * @date: 2020-06-20 14:45
 */
class WanAndroidSquareSystemAdapter : BaseNBAdapter<WanAndroidSystemBean>() {

    override fun getLayoutId(): Int = R.layout.wan_android_recycler_item_vipcn

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

        }
    }
}