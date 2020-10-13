package com.fphoenixcorneae.wanandroid.mvvm.view.adapter

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import com.fphoenixcorneae.viewpager.BaseNBAdapter
import com.fphoenixcorneae.ext.toHtml
import com.fphoenixcorneae.wanandroid.R
import com.fphoenixcorneae.wanandroid.mvvm.model.WanAndroidArticleBean
import kotlinx.android.synthetic.main.wan_android_recycler_item_home_qa.view.*
import java.util.regex.Pattern

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
            mTvTitle.text = data.title.run {
                val pattern = Pattern.compile(
                    "${context.getString(R.string.wan_android_title_fragment_home_qa)}\\s*\\|?\\s*"
                )
                val matcher = pattern.matcher(this)
                when {
                    matcher.find() -> {
                        replaceFirst(matcher.group(), "")
                    }
                    else -> {
                        this
                    }
                }
            }.toHtml()
            mTvDesc.text = data.desc.toHtml()
            mTvNiceDate.text = data.niceDate
        }
    }
}