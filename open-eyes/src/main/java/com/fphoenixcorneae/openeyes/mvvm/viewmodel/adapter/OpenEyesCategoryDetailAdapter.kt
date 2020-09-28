package com.fphoenixcorneae.openeyes.mvvm.viewmodel.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.GradientDrawable
import com.fphoenixcorneae.ext.durationFormat
import com.fphoenixcorneae.framework.glide.GlideUtil
import com.fphoenixcorneae.framework.widget.recyclerview.AbstractRecyclerAdapter
import com.fphoenixcorneae.framework.widget.recyclerview.RecyclerItemType
import com.fphoenixcorneae.framework.widget.recyclerview.ViewHolder
import com.fphoenixcorneae.openeyes.R
import com.fphoenixcorneae.openeyes.mvvm.model.bean.OpenEyesHomeBean
import com.fphoenixcorneae.util.ColorUtil
import kotlinx.android.synthetic.main.open_eyes_item_category_detail.view.*

/**
 * @desc 分类详情适配器
 * @date 2020-09-28 16:33
 */
class OpenEyesCategoryDetailAdapter(
    mContext: Context,
    data: ArrayList<OpenEyesHomeBean.Issue.Item>
) :
    AbstractRecyclerAdapter<OpenEyesHomeBean.Issue.Item>(
        mContext,
        data,
        object : RecyclerItemType<OpenEyesHomeBean.Issue.Item> {
            override fun getLayoutId(item: OpenEyesHomeBean.Issue.Item, position: Int): Int {
                return R.layout.open_eyes_item_category_detail
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
                // duration
                mTvDuration.text = durationFormat(duration)
                // title
                mTvTitle.text = title
                // time
                mTvTag.text = "#${category}"
            }
        }
    }
}
