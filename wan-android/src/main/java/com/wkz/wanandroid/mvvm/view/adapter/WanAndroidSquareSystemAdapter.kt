package com.wkz.wanandroid.mvvm.view.adapter

import android.annotation.SuppressLint
import android.text.Html
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.wkz.adapter.BaseNBAdapter
import com.wkz.wanandroid.R
import com.wkz.wanandroid.mvvm.model.WanAndroidClassifyBean
import com.wkz.wanandroid.mvvm.model.WanAndroidSystemBean
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
            mTvSystemName.text = Html.fromHtml(data.name)
            mRvSystem.apply {
                layoutManager = FlexboxLayoutManager(context).apply {
                    // 方向 主轴为水平方向，起点在左端
                    flexDirection = FlexDirection.ROW
                    // 左对齐
                    justifyContent = JustifyContent.FLEX_START
                }
                setHasFixedSize(true)
                isNestedScrollingEnabled = false
                setItemViewCacheSize(200)
                adapter = WanAndroidSquareSystemChildAdapter().apply {
                    dataList.addAll(data.children)
                    onItemClickListener = object : OnItemClickListener<WanAndroidClassifyBean> {
                        override fun onItemClick(item: WanAndroidClassifyBean, position: Int) {

                        }
                    }
                }
            }
        }
    }
}