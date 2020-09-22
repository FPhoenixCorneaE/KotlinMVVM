package com.fphoenixcorneae.kotlinmvvm.mvvm.viewmodel.adapter

import android.content.Context
import android.graphics.drawable.GradientDrawable
import androidx.recyclerview.widget.RecyclerView
import com.fphoenixcorneae.ext.dp2px
import com.fphoenixcorneae.ext.durationFormat
import com.fphoenixcorneae.ext.isNonNullAndNotEmpty
import com.fphoenixcorneae.framework.glide.GlideUtil
import com.fphoenixcorneae.framework.widget.recyclerview.AbstractRecyclerAdapter
import com.fphoenixcorneae.framework.widget.recyclerview.RecyclerItemType
import com.fphoenixcorneae.framework.widget.recyclerview.ViewHolder
import com.fphoenixcorneae.kotlinmvvm.R
import com.fphoenixcorneae.kotlinmvvm.mvvm.model.bean.OpenEyesHomeBean
import com.fphoenixcorneae.util.ColorUtil
import kotlinx.android.synthetic.main.open_eyes_item_follow_child.view.*

/**
 * @desc 发现-关注子适配器
 * @date 2020-09-21 16:46
 */
class OpenEyesFollowChildAdapter(
    mContext: Context,
    data: ArrayList<OpenEyesHomeBean.Issue.Item>
) :
    AbstractRecyclerAdapter<OpenEyesHomeBean.Issue.Item>(
        mContext,
        data,
        object : RecyclerItemType<OpenEyesHomeBean.Issue.Item> {
            override fun getLayoutId(item: OpenEyesHomeBean.Issue.Item, position: Int): Int {
                return R.layout.open_eyes_item_follow_child
            }
        }) {

    /**
     * 绑定数据
     */
    override fun bindData(holder: ViewHolder, data: OpenEyesHomeBean.Issue.Item, position: Int) {
        with(holder.itemView) {
            (layoutParams as RecyclerView.LayoutParams).apply {
                marginStart = context.dp2px(16f)
                marginEnd = when (position) {
                    itemCount - 1 -> context.dp2px(16f)
                    else -> context.dp2px(0f)
                }
            }
            data.data?.apply {
                // cover
                GlideUtil.setupRoundedImage(
                    mIvCoverFeed,
                    cover.feed,
                    context.dp2px(8f),
                    GradientDrawable().apply {
                        setColor(ColorUtil.randomColor)
                    }
                )
                // title
                mTvTitle.text = title
                // tag
                mTvTag.text = kotlin.run {
                    // 格式化时间
                    val timeFormat = durationFormat(duration)
                    if (tags.isNonNullAndNotEmpty()) {
                        "#${tags[0].name} / $timeFormat"
                    } else {
                        "#$timeFormat"
                    }
                }
            }
        }
    }
}
