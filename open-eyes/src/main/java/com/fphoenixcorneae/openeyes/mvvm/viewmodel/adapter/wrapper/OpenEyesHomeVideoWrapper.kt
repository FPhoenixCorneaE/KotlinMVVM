package com.fphoenixcorneae.openeyes.mvvm.viewmodel.adapter.wrapper

import android.annotation.SuppressLint
import android.graphics.drawable.GradientDrawable
import com.fphoenixcorneae.viewpager.internal.ViewHolder
import com.fphoenixcorneae.viewpager.wrapper.ViewHolderWrapper
import com.fphoenixcorneae.ext.dpToPx
import com.fphoenixcorneae.ext.durationFormat
import com.fphoenixcorneae.framework.glide.GlideUtil
import com.fphoenixcorneae.openeyes.R
import com.fphoenixcorneae.openeyes.mvvm.model.bean.OpenEyesHomeBean
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
            item.data?.apply {
                // avatar
                GlideUtil.setupImagePlaceDrawableRes(
                    mIvAvatar,
                    author?.icon ?: provider?.icon,
                    R.drawable.open_eyes_ic_avatar_default
                )
                // author name
                mTvAuthorName.text = author?.name ?: provider?.name
                // author description
                mTvAuthorDescription.text = author?.description ?: provider?.alias
                // category
                mTvCategory.text = "#${category}"
                // cover
                GlideUtil.setupImage(
                    mIvCoverFeed,
                    cover.feed,
                    GradientDrawable().apply {
                        setColor(ColorUtil.randomColor)
                        cornerRadius = context.dpToPx(8f)
                    }
                )
                // duration
                mTvDuration.text = durationFormat(duration)
                // title
                mTvTitle.text = title
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
                            expanded = isExpanded()
                        }
                    }
                    setText(description, expanded)
                }
            }
        }
    }
}