package com.wkz.wanandroid.mvvm.view.wrapper

import com.wkz.adapter.internal.ViewHolder
import com.wkz.adapter.wrapper.ViewHolderWrapper
import com.wkz.framework.glide.GlideUtil
import com.wkz.wanandroid.R
import com.wkz.wanandroid.mvvm.model.WanAndroidPageBean
import kotlinx.android.synthetic.main.wan_android_item_home_article.view.*

/**
 * @desc: 首页文章适配器
 * @date: 2019-10-28 16:03
 */
class WanAndroidHomeArticleWrapper :
    ViewHolderWrapper<WanAndroidPageBean.ArticleBean>(R.layout.wan_android_item_home_article) {

    override fun onBindViewHolder(holder: ViewHolder, item: WanAndroidPageBean.ArticleBean) {
        GlideUtil.setupImage(holder.itemView.mIvIcon, getIcon(item.link))
        holder.itemView.mTvTitle.text = item.title
        holder.itemView.mSuperChapterName.text = item.superChapterName
        holder.itemView.mChapterName.text = item.chapterName
        holder.itemView.mNiceDate.text = item.niceDate
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