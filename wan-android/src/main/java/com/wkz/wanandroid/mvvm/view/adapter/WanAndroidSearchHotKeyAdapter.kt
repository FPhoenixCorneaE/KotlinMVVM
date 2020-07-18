package com.wkz.wanandroid.mvvm.view.adapter

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import com.wkz.adapter.BaseNBAdapter
import com.wkz.util.ColorUtil
import com.wkz.wanandroid.R
import com.wkz.wanandroid.mvvm.model.WanAndroidSearchBean
import kotlinx.android.synthetic.main.wan_android_recycler_item_square_navigation_child.view.*

/**
 * @desc: 搜索热词适配器
 * @date: 2020-07-18 17:23
 */
class WanAndroidSearchHotKeyAdapter : BaseNBAdapter<WanAndroidSearchBean>() {

    override fun getLayoutId(): Int = R.layout.wan_android_recycler_item_search_hot_key

    /**
     * 绑定数据
     */
    @SuppressLint("NewApi")
    override fun onBindData(
        viewHolder: RecyclerView.ViewHolder,
        data: WanAndroidSearchBean,
        position: Int
    ) {
        viewHolder.itemView.apply {
            mTvName.text = data.name
            mTvName.setTextColor(ColorUtil.randomColor)
        }
    }
}