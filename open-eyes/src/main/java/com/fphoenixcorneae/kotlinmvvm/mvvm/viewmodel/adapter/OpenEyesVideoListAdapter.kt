package com.fphoenixcorneae.kotlinmvvm.mvvm.viewmodel.adapter

import android.content.Context
import android.graphics.drawable.GradientDrawable
import com.fphoenixcorneae.extension.durationFormat
import com.fphoenixcorneae.framework.glide.GlideUtil
import com.fphoenixcorneae.framework.widget.recyclerview.AbstractRecyclerAdapter
import com.fphoenixcorneae.framework.widget.recyclerview.RecyclerItemType
import com.fphoenixcorneae.framework.widget.recyclerview.ViewHolder
import com.fphoenixcorneae.kotlinmvvm.R
import com.fphoenixcorneae.kotlinmvvm.mvvm.model.bean.OpenEyesHomeBean
import com.fphoenixcorneae.util.ColorUtil
import com.fphoenixcorneae.util.SizeUtil
import kotlinx.android.synthetic.main.open_eyes_item_video_small_card.view.*
import kotlinx.android.synthetic.main.open_eyes_item_video_text_card.view.*

/**
 * desc: 视频列表适配器
 */
class OpenEyesVideoListAdapter(mContext: Context, data: ArrayList<OpenEyesHomeBean.Issue.Item>) :
    AbstractRecyclerAdapter<OpenEyesHomeBean.Issue.Item>(
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
     * 添加相关推荐等数据 Item
     */
    fun setData(item: ArrayList<OpenEyesHomeBean.Issue.Item>) {
        mData.clear()
        mData.addAll(item)
        notifyDataSetChanged()
    }

    /**
     * Kotlin的函数可以作为参数，写callback的时候，可以不用interface了
     */
    private var mOnItemClickRelatedVideo: ((item: OpenEyesHomeBean.Issue.Item) -> Unit)? = null

    fun setOnItemDetailClick(mItemRelatedVideo: (item: OpenEyesHomeBean.Issue.Item) -> Unit) {
        this.mOnItemClickRelatedVideo = mItemRelatedVideo
    }

    /**
     * 绑定数据
     */
    override fun bindData(holder: ViewHolder, data: OpenEyesHomeBean.Issue.Item, position: Int) {
        with(holder.itemView) {
            when (data.type) {
                "textCard" -> {
                    mTvTextCard.text = data.data?.text
                }
                "videoSmallCard" -> {
                    // title
                    mTvTitle.text = data.data?.title
                    // duration
                    mTvDuration.text = durationFormat(data.data?.duration ?: 0)
                    // cover
                    val placeholder = GradientDrawable()
                    placeholder.setColor(ColorUtil.randomColor)
                    placeholder.cornerRadius = SizeUtil.dp2px(4F).toFloat()
                    GlideUtil.setupRoundedImage(
                        mIvVideoSmallCard,
                        data.data?.cover?.detail,
                        SizeUtil.dp2px(4F),
                        placeholder
                    )

                    // 判断onItemClickRelatedVideo 并使用
                    holder.itemView.setOnClickListener { mOnItemClickRelatedVideo?.invoke(data) }
                }
            }
        }
    }
}
