package com.fphoenixcorneae.kotlinmvvm.mvvm.viewmodel.adapter

import android.content.Context
import android.graphics.drawable.GradientDrawable
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fphoenixcorneae.ext.dp2px
import com.fphoenixcorneae.framework.glide.GlideUtil
import com.fphoenixcorneae.framework.widget.recyclerview.AbstractRecyclerAdapter
import com.fphoenixcorneae.framework.widget.recyclerview.RecyclerItemType
import com.fphoenixcorneae.framework.widget.recyclerview.ViewHolder
import com.fphoenixcorneae.kotlinmvvm.R
import com.fphoenixcorneae.kotlinmvvm.mvvm.model.bean.OpenEyesHomeBean
import com.fphoenixcorneae.util.ColorUtil
import kotlinx.android.synthetic.main.open_eyes_item_follow.view.*
import kotlinx.android.synthetic.main.open_eyes_item_video_small_card.view.mTvTitle

/**
 * @desc 发现-关注适配器
 * @date 2020-09-21 15:05
 */
class OpenEyesFollowAdapter(
    mContext: Context,
    data: ArrayList<OpenEyesHomeBean.Issue.Item>
) :
    AbstractRecyclerAdapter<OpenEyesHomeBean.Issue.Item>(
        mContext,
        data,
        object : RecyclerItemType<OpenEyesHomeBean.Issue.Item> {
            override fun getLayoutId(item: OpenEyesHomeBean.Issue.Item, position: Int): Int {
                return R.layout.open_eyes_item_follow
            }
        }) {

    /**
     * 绑定数据
     */
    override fun bindData(holder: ViewHolder, data: OpenEyesHomeBean.Issue.Item, position: Int) {
        with(holder.itemView) {
            (layoutParams as RecyclerView.LayoutParams).apply {
                topMargin = if (position != 0) {
                    context.dp2px(16f)
                } else {
                    context.dp2px(0f)
                }
            }
            data.data?.apply {
                // avatar
                GlideUtil.setupImage(
                    mIvAvatar,
                    header.icon,
                    GradientDrawable().apply {
                        setColor(ColorUtil.randomColor)
                    }
                )
                // title
                mTvTitle.text = header.title
                // description
                mTvDesc.text = header.description
                // video
                mRvRecycler.apply {
                    setHasFixedSize(true)
                    layoutManager =
                        LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                    adapter = OpenEyesFollowChildAdapter(context, itemList)
                }
            }
        }
    }
}
