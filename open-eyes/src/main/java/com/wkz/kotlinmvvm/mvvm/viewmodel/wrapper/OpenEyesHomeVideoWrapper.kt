package com.wkz.kotlinmvvm.mvvm.viewmodel.wrapper

import com.wkz.adapter.internal.ViewHolder
import com.wkz.adapter.wrapper.ViewHolderWrapper
import com.wkz.framework.glide.GlideUtil
import com.wkz.kotlinmvvm.R
import com.wkz.kotlinmvvm.mvvm.model.bean.OpenEyesHomeBean
import com.wkz.util.SizeUtil
import kotlinx.android.synthetic.main.open_eyes_item_home_video.view.*

/**
 * @desc: 首页精选视频 Wrapper
 */
class OpenEyesHomeVideoWrapper :
    ViewHolderWrapper<OpenEyesHomeBean.Issue.Item>(R.layout.open_eyes_item_home_video) {

    override fun onBindViewHolder(holder: ViewHolder, item: OpenEyesHomeBean.Issue.Item) {
        GlideUtil.setupRoundedImage(
            holder.itemView.mIvCoverFeed,
            item.data?.cover?.feed,
            SizeUtil.dp2px(8F)
        )
        GlideUtil.setupCircleImage(
            holder.itemView.mIvAvatar,
            item.data?.author?.icon ?: item.data?.provider?.icon
        )
        holder.itemView.mTvAuthor.text = item.data?.title
    }
}