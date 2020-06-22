package com.wkz.wanandroid.mvvm.view.adapter

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import com.wkz.adapter.BaseNBAdapter
import com.wkz.extension.toHtml
import com.wkz.util.ColorUtil
import com.wkz.wanandroid.R
import com.wkz.wanandroid.mvvm.model.WanAndroidArticleBean
import kotlinx.android.synthetic.main.wan_android_recycler_item_square_navigation_child.view.*

/**
 * @desc: 广场导航子适配器
 * @date: 2020-06-22 10:45
 */
class WanAndroidSquareNavigationChildAdapter : BaseNBAdapter<WanAndroidArticleBean>() {

    override fun getLayoutId(): Int = R.layout.wan_android_recycler_item_square_navigation_child

    /**
     * 绑定数据
     */
    @SuppressLint("NewApi")
    override fun onBindData(
        viewHolder: RecyclerView.ViewHolder,
        data: WanAndroidArticleBean,
        position: Int
    ) {
        viewHolder.itemView.apply {
            mTvName.text = data.title.toHtml()
            mTvName.setTextColor(ColorUtil.randomColor)
        }
    }
}