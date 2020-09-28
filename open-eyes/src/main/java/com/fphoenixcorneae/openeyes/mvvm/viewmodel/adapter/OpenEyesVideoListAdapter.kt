package com.fphoenixcorneae.openeyes.mvvm.viewmodel.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.GradientDrawable
import com.fphoenixcorneae.ext.dpToPx
import com.fphoenixcorneae.ext.durationFormat
import com.fphoenixcorneae.framework.glide.GlideUtil
import com.fphoenixcorneae.framework.widget.recyclerview.AbstractRecyclerAdapter
import com.fphoenixcorneae.framework.widget.recyclerview.RecyclerItemType
import com.fphoenixcorneae.framework.widget.recyclerview.ViewHolder
import com.fphoenixcorneae.openeyes.R
import com.fphoenixcorneae.openeyes.mvvm.model.bean.OpenEyesHomeBean
import com.fphoenixcorneae.util.ColorUtil.Companion.randomColor
import kotlinx.android.synthetic.main.open_eyes_item_video_small_card.view.*
import kotlinx.android.synthetic.main.open_eyes_item_video_text_card.view.*

/**
 * @desc 视频列表适配器
 */
class OpenEyesVideoListAdapter(
    mContext: Context,
    data: ArrayList<OpenEyesHomeBean.Issue.Item>
) : AbstractRecyclerAdapter<OpenEyesHomeBean.Issue.Item>(
    mContext,
    data,
    object : RecyclerItemType<OpenEyesHomeBean.Issue.Item> {
        override fun getLayoutId(item: OpenEyesHomeBean.Issue.Item, position: Int): Int {
            return when (data[position].type) {
                "textCard" ->
                    R.layout.open_eyes_item_video_text_card
                "videoSmallCard" ->
                    R.layout.open_eyes_item_video_small_card
                else -> 0
            }
        }
    }) {

    /**
     * 绑定数据
     */
    @SuppressLint("SetTextI18n")
    override fun bindData(holder: ViewHolder, data: OpenEyesHomeBean.Issue.Item, position: Int) {
        with(holder.itemView) {
            data.data?.apply {
                when (data.type) {
                    "textCard" -> {
                        mTvTextCard.text = text
                    }
                    "videoSmallCard" -> {
                        // title
                        mTvTitle.text = title
                        // category
                        mTvCategory.text = "#${category}"
                        // duration
                        mTvDuration.text = durationFormat(duration)
                        // cover
                        GlideUtil.setupImage(
                            mIvVideoSmallCard,
                            cover.detail,
                            GradientDrawable().apply {
                                setColor(randomColor)
                                cornerRadius = context.dpToPx(8f)
                            }
                        )
                    }
                }
            }
        }
    }
}
