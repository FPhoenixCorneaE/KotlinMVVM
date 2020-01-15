package com.wkz.kotlinmvvm.mvvm.viewmodel.wrapper

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import com.wkz.adapter.internal.ViewHolder
import com.wkz.adapter.wrapper.ViewHolderWrapper
import com.wkz.extension.durationFormat
import com.wkz.framework.glide.GlideUtil
import com.wkz.kotlinmvvm.R
import com.wkz.kotlinmvvm.mvvm.model.bean.OpenEyesHomeBean
import com.wkz.util.ColorUtil
import com.wkz.util.ResourceUtil
import com.wkz.util.SizeUtil
import com.wkz.widget.Callback
import kotlinx.android.synthetic.main.open_eyes_item_home_video.view.*

/**
 * @desc: 首页精选视频 Wrapper
 */
class OpenEyesHomeVideoWrapper :
    ViewHolderWrapper<OpenEyesHomeBean.Issue.Item>(R.layout.open_eyes_item_home_video) {

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, item: OpenEyesHomeBean.Issue.Item) {
        with(holder.itemView) {
            // avatar
            GlideUtil.setupCircleImagePlaceDrawableRes(
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
            val placeholder = GradientDrawable()
            placeholder.setColor(ColorUtil.randomColor)
            placeholder.cornerRadius = SizeUtil.dp2px(8F).toFloat()
            GlideUtil.setupRoundedImage(
                mIvCoverFeed,
                item.data?.cover?.feed,
                SizeUtil.dp2px(8F),
                placeholder
            )
            // duration
            mTvDuration.text = durationFormat(item.data?.duration ?: 0)
            // title
            mTvTitle.text = item.data?.title
            // description
            mTvDescription.apply {
                // 设置最大显示行数
                mMaxLineCount = 3
                // 收起文案
                mCollapseText = ResourceUtil.getString(R.string.open_eyes_collapse_text)
                // 展开文案
                mExpandText = ResourceUtil.getString(R.string.open_eyes_expand_text)
                // 是否支持收起功能
                mCollapseEnable = true
                // 是否给展开收起添加下划线
                mUnderlineEnable = false
                // 收起文案颜色
                mCollapseTextColor = Color.BLUE
                // 展开文案颜色
                mExpandTextColor = Color.RED
                item.data?.description?.let {
                    setText(it, item.data.expanded, object : Callback {
                        override fun onExpand() {

                        }

                        override fun onCollapse() {
                        }

                        override fun onLoss() {
                        }

                        override fun onExpandClick() {
                            item.data.expanded = !item.data.expanded
                            changeExpandedState(item.data.expanded)
                        }

                        override fun onCollapseClick() {
                            item.data.expanded = !item.data.expanded
                            changeExpandedState(item.data.expanded)
                        }
                    })
                }
            }
        }
    }
}