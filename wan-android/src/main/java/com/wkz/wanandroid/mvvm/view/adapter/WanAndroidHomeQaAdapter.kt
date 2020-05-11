package com.wkz.wanandroid.mvvm.view.adapter

import android.annotation.SuppressLint
import android.text.Html
import androidx.recyclerview.widget.RecyclerView
import com.wkz.adapter.BaseNBAdapter
import com.wkz.wanandroid.R
import com.wkz.wanandroid.mvvm.model.WanAndroidPageBean
import kotlinx.android.synthetic.main.wan_android_recycler_item_home_qa.view.*

/**
 * @desc: 首页问答适配器
 * @date: 2019-11-06 11:21
 */
class WanAndroidHomeQaAdapter :
    BaseNBAdapter<WanAndroidPageBean.ArticleBean>() {
    override fun getLayoutId(): Int = R.layout.wan_android_recycler_item_home_qa

    /**
     * 绑定数据
     */
    @SuppressLint("NewApi")
    override fun onBindData(
        viewHolder: RecyclerView.ViewHolder,
        data: WanAndroidPageBean.ArticleBean,
        position: Int
    ) {
        viewHolder.itemView.apply {
            mTvTitle.text = data.title.replaceFirst("每日一问 ", "")
            mTvDesc.text = Html.fromHtml(data.desc)
            mNiceDate.text = data.niceDate
        }
    }
}