package com.fphoenixcorneae.wanandroid.mvvm.view.adapter

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import com.fphoenixcorneae.adapter.BaseNBAdapter
import com.fphoenixcorneae.ext.isVisible
import com.fphoenixcorneae.ext.toHtml
import com.fphoenixcorneae.ext.visible
import com.fphoenixcorneae.wanandroid.R
import com.fphoenixcorneae.wanandroid.mvvm.model.WanAndroidArticleBean
import kotlinx.android.synthetic.main.wan_android_recycler_item_search_result.view.*

/**
 * @desc: 搜索结果适配器
 * @date: 2020-07-30 14:38
 */
class WanAndroidSearchResultAdapter :
    BaseNBAdapter<WanAndroidArticleBean>() {

    override fun getLayoutId(): Int = R.layout.wan_android_recycler_item_search_result

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
            mTvAuthor.text =
                when {
                    data.author.isNotEmpty() -> {
                        data.author
                    }
                    else -> {
                        data.shareUser
                    }
                }
            mTvTop.isVisible = data.type == 1
            mTvNew.isVisible = data.fresh
            mTvTag.isVisible = data.tags.isNotEmpty()
            if (data.tags.isNotEmpty()) {
                mTvTag.text = data.tags[0].name
            }
            mTvTitle.text = data.title.toHtml()
            mSuperChapterName.text = data.superChapterName
            mChapterName.text = data.chapterName
            mTvNiceDate.text = data.niceDate
            mSbCollect.setChecked(data.collect)
            mTvAuthor.setOnClickListener {
                // 作者点击
                onItemChildClickListener?.onItemChild1Click(it, data, position)
            }
            mSbCollect.setOnClickListener {
                // 收藏Icon点击
                onItemChildClickListener?.onItemChild2Click(it, data, position)
            }
        }
    }
}