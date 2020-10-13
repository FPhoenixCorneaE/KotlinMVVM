package com.fphoenixcorneae.wanandroid.mvvm.view.adapter

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import com.fphoenixcorneae.viewpager.BaseNBAdapter
import com.fphoenixcorneae.ext.view.gone
import com.fphoenixcorneae.ext.toHtml
import com.fphoenixcorneae.wanandroid.R
import com.fphoenixcorneae.wanandroid.mvvm.model.WanAndroidCollectArticleBean
import kotlinx.android.synthetic.main.wan_android_recycler_item_collect_article.view.*

/**
 * @desc: 收藏文章适配器
 * @date: 2020-07-31 14:01
 */
class WanAndroidCollectArticleAdapter :
    BaseNBAdapter<WanAndroidCollectArticleBean>() {

    override fun getLayoutId(): Int = R.layout.wan_android_recycler_item_collect_article

    /**
     * 绑定数据
     */
    @SuppressLint("NewApi")
    override fun onBindData(
        viewHolder: RecyclerView.ViewHolder,
        data: WanAndroidCollectArticleBean,
        position: Int
    ) {
        viewHolder.itemView.apply {
            mTvAuthor.text =
                when {
                    data.author.isNotEmpty() -> {
                        data.author
                    }
                    else -> {
                        context.getString(R.string.wan_android_anonymous_user)
                    }
                }
            mTvTop.gone()
            mTvNew.gone()
            mTvTag.gone()
            mTvTitle.text = data.title.toHtml()
            mSuperChapterName.text = data.chapterName
            mChapterName.gone()
            mTvNiceDate.text = data.niceDate
            mTvAuthor.setOnClickListener {
                // 作者点击
                onItemChildClickListener?.onItemChild1Click(it, data, position)
            }
        }
    }
}