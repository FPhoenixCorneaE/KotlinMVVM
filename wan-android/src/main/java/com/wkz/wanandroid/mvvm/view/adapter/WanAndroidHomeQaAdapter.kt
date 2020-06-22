package com.wkz.wanandroid.mvvm.view.adapter

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import com.wkz.adapter.BaseNBAdapter
import com.wkz.extension.toHtml
import com.wkz.wanandroid.R
import com.wkz.wanandroid.mvvm.model.WanAndroidArticleBean
import kotlinx.android.synthetic.main.wan_android_recycler_item_home_qa.view.*

/**
 * @desc: 首页问答适配器
 * @date: 2019-11-06 11:21
 */
class WanAndroidHomeQaAdapter :
    BaseNBAdapter<WanAndroidArticleBean>() {
    override fun getLayoutId(): Int = R.layout.wan_android_recycler_item_home_qa

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
            mTvTitle.text = data.title.replaceFirst("每日一问 ", "")
            mTvDesc.text = data.desc.toHtml()
            mTvNiceDate.text = data.niceDate
        }
    }
}