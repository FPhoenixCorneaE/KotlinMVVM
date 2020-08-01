package com.wkz.wanandroid.mvvm.view.adapter

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import com.wkz.adapter.BaseNBAdapter
import com.wkz.wanandroid.R
import com.wkz.wanandroid.mvvm.model.WanAndroidCollectWebsiteBean
import kotlinx.android.synthetic.main.wan_android_recycler_item_collect_website.view.*

/**
 * @desc: 收藏网址适配器
 * @date: 2020-07-31 17:25
 */
class WanAndroidCollectWebsiteAdapter :
    BaseNBAdapter<WanAndroidCollectWebsiteBean>() {

    override fun getLayoutId(): Int = R.layout.wan_android_recycler_item_collect_website

    /**
     * 绑定数据
     */
    @SuppressLint("NewApi")
    override fun onBindData(
        viewHolder: RecyclerView.ViewHolder,
        data: WanAndroidCollectWebsiteBean,
        position: Int
    ) {
        viewHolder.itemView.apply {
            mTvName.text = data.name
            mTvLink.text = data.link
        }
    }
}