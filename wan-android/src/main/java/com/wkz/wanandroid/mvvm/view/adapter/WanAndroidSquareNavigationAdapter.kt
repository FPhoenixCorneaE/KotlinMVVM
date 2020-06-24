package com.wkz.wanandroid.mvvm.view.adapter

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.wkz.adapter.BaseNBAdapter
import com.wkz.extension.toHtml
import com.wkz.wanandroid.R
import com.wkz.wanandroid.mvvm.model.WanAndroidArticleBean
import com.wkz.wanandroid.mvvm.model.WanAndroidNavigationBean
import kotlinx.android.synthetic.main.wan_android_recycler_item_square_system.view.*

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
            mTvSystemName.text = data.name.toHtml()
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
                adapter = WanAndroidSquareNavigationChildAdapter().apply {
                    dataList.addAll(data.articles)
                    onItemClickListener = object : OnItemClickListener<WanAndroidArticleBean> {
                        override fun onItemClick(item: WanAndroidArticleBean, position: Int) {
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
}