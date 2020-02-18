package com.wkz.wanandroid.mvvm.view.adapter

import android.annotation.SuppressLint
import android.text.Html
import androidx.recyclerview.widget.RecyclerView
import com.wkz.adapter.BaseNBAdapter
import com.wkz.extension.visible
import com.wkz.framework.glide.GlideUtil
import com.wkz.util.SizeUtil
import com.wkz.wanandroid.R
import com.wkz.wanandroid.mvvm.model.WanAndroidPageBean
import kotlinx.android.synthetic.main.wan_android_recycler_item_home_article.view.*

/**
 * @desc: 首页文章适配器
 * @date: 2019-10-28 16:03
 */
class WanAndroidHomeArticleAdapter :
    BaseNBAdapter<WanAndroidPageBean.ArticleBean>() {
    override fun getLayoutId(): Int = R.layout.wan_android_recycler_item_home_article

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
            mTvTop.visible(data.type == 1)
            mTvNew.visible(data.fresh)
            mTvTag.visible(data.tags.isNotEmpty())
            if (data.tags.isNotEmpty()) {
                mTvTag.text = data.tags[0].name
            }
            mTvTitle.text = Html.fromHtml(data.title)
            mSuperChapterName.text = data.superChapterName
            mChapterName.text = data.chapterName
            mNiceDate.text = data.niceDate
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