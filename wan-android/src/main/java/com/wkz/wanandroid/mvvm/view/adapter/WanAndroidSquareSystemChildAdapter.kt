package com.wkz.wanandroid.mvvm.view.adapter

import android.annotation.SuppressLint
import android.text.Html
import androidx.recyclerview.widget.RecyclerView
import com.wkz.adapter.BaseNBAdapter
import com.wkz.util.ColorUtil
import com.wkz.wanandroid.R
import com.wkz.wanandroid.mvvm.model.WanAndroidClassifyBean
import kotlinx.android.synthetic.main.wan_android_recycler_item_square_system_child.view.*

/**
 * @desc: 广场体系子适配器
 * @date: 2020-06-20 14:45
 */
class WanAndroidSquareSystemChildAdapter : BaseNBAdapter<WanAndroidClassifyBean>() {

    override fun getLayoutId(): Int = R.layout.wan_android_recycler_item_square_system_child

    /**
     * 绑定数据
     */
    @SuppressLint("NewApi")
    override fun onBindData(
        viewHolder: RecyclerView.ViewHolder,
        data: WanAndroidClassifyBean,
        position: Int
    ) {
        viewHolder.itemView.apply {
            mTvName.text = Html.fromHtml(data.name)
            mTvName.setTextColor(ColorUtil.randomColor)
        }
    }
}