package com.fphoenixcorneae.wanandroid.mvvm.view.adapter

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import com.fphoenixcorneae.viewpager.BaseNBAdapter
import com.fphoenixcorneae.ext.view.isVisible
import com.fphoenixcorneae.ext.toHtml
import com.fphoenixcorneae.framework.glide.GlideUtil
import com.fphoenixcorneae.util.SizeUtil
import com.fphoenixcorneae.wanandroid.R
import com.fphoenixcorneae.wanandroid.mvvm.model.WanAndroidArticleBean
import kotlinx.android.synthetic.main.wan_android_recycler_item_square_article.view.*

/**
 * @desc: 广场文章适配器
 * @date: 2020-06-24 16:15
 */
class WanAndroidSquareArticleAdapter :
    BaseNBAdapter<WanAndroidArticleBean>() {

    override fun getLayoutId(): Int = R.layout.wan_android_recycler_item_square_article

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
            GlideUtil.setupRoundedImage(
                mIvIcon,
                getIcon(data.link),
                SizeUtil.dp2px(4f)
            )
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

    private fun getIcon(link: String): Int {
        return when {
            link.contains("wanandroid.com") -> R.mipmap.wan_android_ic_logo_android
            link.contains("www.jianshu.com") -> R.mipmap.wan_android_ic_logo_jianshu
            link.contains("juejin.im") -> R.mipmap.wan_android_ic_logo_juejin
            link.contains("blog.csdn.net") -> R.mipmap.wan_android_ic_logo_csdn
            link.contains("weixin.qq.com") -> R.mipmap.wan_android_ic_logo_wechat
            else -> R.mipmap.wan_android_ic_logo_other
        }
    }
}