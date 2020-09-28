package com.fphoenixcorneae.kotlinmvvm.mvvm.viewmodel.adapter.wrapper

import android.annotation.SuppressLint
import android.graphics.drawable.GradientDrawable
import com.fphoenixcorneae.adapter.internal.ViewHolder
import com.fphoenixcorneae.adapter.wrapper.ViewHolderWrapper
import com.fphoenixcorneae.ext.dpToPx
import com.fphoenixcorneae.ext.durationFormat
import com.fphoenixcorneae.framework.glide.GlideUtil
import com.fphoenixcorneae.kotlinmvvm.R
import com.fphoenixcorneae.kotlinmvvm.mvvm.model.bean.OpenEyesHomeBean
import com.fphoenixcorneae.util.ColorUtil
import com.fphoenixcorneae.util.ResourceUtil
import com.fphoenixcorneae.widget.ExpandCollapseTextView
import kotlinx.android.synthetic.main.open_eyes_item_home_video.view.*

/**
 * @desc 首页精选视频 Wrapper
 */
class OpenEyesHomeVideoWrapper :
    ViewHolderWrapper<OpenEyesHomeBean.Issue.Item>(R.layout.open_eyes_item_home_video) {

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, item: OpenEyesHomeBean.Issue.Item) {
        with(holder.itemView) {
            // avatar
            GlideUtil.setupImagePlaceDrawableRes(
                mIvAvatar,
                item.data?.author?.icon ?: item.data?.provider?.icon,
                R.drawable.open_eyes_ic_avatar_default
            )
            // author name
            mTvAuthorName.text = item.data?.author?.name
            // author description
            mTvAuthorDescription.text = item.data?.author?.description
            // category
            mTvCategory.text = "#${item.data?.category}"
            // cover
            GlideUtil.setupImage(
                mIvCoverFeed,
                item.data?.cover?.feed,
                GradientDrawable().apply {
                    setColor(ColorUtil.randomColor)
                    cornerRadius = context.dpToPx(8f)
                }
            )
            // duration
            mTvDuration.text = durationFormat(item.data?.duration ?: 0)
            // title
            mTvTitle.text = item.data?.title
            // description
            mTvDescription.apply {
                // 设置最大显示行数
                mMaxLineCount = 2
                // 收起文案
                mCollapseText = ResourceUtil.getString(R.string.open_eyes_collapse_text)
                // 展开文案
                mExpandText = ResourceUtil.getString(R.string.open_eyes_expand_text)
                // 是否支持收起功能
                mCollapseEnable = true
                // 是否给展开收起添加下划线
                mUnderlineEnable = false
                // 收起文案颜色
                mCollapseTextColor = ResourceUtil.getColor(R.color.open_eyes_color_black)
                // 展开文案颜色
                mExpandTextColor = ResourceUtil.getColor(R.color.open_eyes_color_black)
                // 文字状态改变监听器
                mOnTextStateChangedListener = { state ->
                    if (state == ExpandCollapseTextView.TextState.Expanded
                        || state == ExpandCollapseTextView.TextState.Collapsed
                    ) {
                        item.data?.let {
                            it.expanded = isExpanded()
                        }
                    }
                }
                item.data?.let {
                    setText(it.description, item.data.expanded)
                }
            }
        }
    }
}