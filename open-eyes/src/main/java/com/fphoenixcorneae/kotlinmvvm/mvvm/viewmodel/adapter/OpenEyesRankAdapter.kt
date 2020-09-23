package com.fphoenixcorneae.kotlinmvvm.mvvm.viewmodel.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.GradientDrawable
import com.fphoenixcorneae.ext.durationFormat
import com.fphoenixcorneae.framework.glide.GlideUtil
import com.fphoenixcorneae.framework.widget.recyclerview.AbstractRecyclerAdapter
import com.fphoenixcorneae.framework.widget.recyclerview.RecyclerItemType
import com.fphoenixcorneae.framework.widget.recyclerview.ViewHolder
import com.fphoenixcorneae.kotlinmvvm.R
import com.fphoenixcorneae.kotlinmvvm.mvvm.model.bean.OpenEyesHomeBean
import com.fphoenixcorneae.util.ColorUtil
import kotlinx.android.synthetic.main.open_eyes_item_rank.view.*

/**
 * @desc 排行榜适配器
 * @date 2020-09-23 14:39
 */
class OpenEyesRankAdapter(
    mContext: Context,
    data: ArrayList<OpenEyesHomeBean.Issue.Item>
) :
    AbstractRecyclerAdapter<OpenEyesHomeBean.Issue.Item>(
        mContext,
        data,
        object : RecyclerItemType<OpenEyesHomeBean.Issue.Item> {
            override fun getLayoutId(item: OpenEyesHomeBean.Issue.Item, position: Int): Int {
                return R.layout.open_eyes_item_rank
            }
        }) {

    /**
     * 绑定数据
     */
    @SuppressLint("SetTextI18n")
    override fun bindData(holder: ViewHolder, data: OpenEyesHomeBean.Issue.Item, position: Int) {
        with(holder.itemView) {
            data.data?.apply {
                // bgPicture
                GlideUtil.setupImage(
                    mIvCoverFeed,
                    cover.feed,
                    GradientDrawable().apply {
                        setColor(ColorUtil.randomColor)
                    }
                )
                // title
                mTvTitle.text = title
                // time
                val timeFormat = durationFormat(duration)
                mTvTag.text = "#${category}/$timeFormat"
            }
        }
    }
}
