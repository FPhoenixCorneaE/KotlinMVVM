package com.fphoenixcorneae.wanandroid.mvvm.view.adapter

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import com.fphoenixcorneae.adapter.BaseNBAdapter
import com.fphoenixcorneae.ext.toHtml
import com.fphoenixcorneae.wanandroid.R
import com.fphoenixcorneae.wanandroid.mvvm.model.WanAndroidArticleBean
import kotlinx.android.synthetic.main.wan_android_recycler_item_vipcn.view.*

/**
 * @desc: 公众号适配器
 * @date: 2020-06-18 17:42
 */
class WanAndroidVipcnAdapter :
    BaseNBAdapter<WanAndroidArticleBean>() {

    override fun getLayoutId(): Int = R.layout.wan_android_recycler_item_vipcn

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