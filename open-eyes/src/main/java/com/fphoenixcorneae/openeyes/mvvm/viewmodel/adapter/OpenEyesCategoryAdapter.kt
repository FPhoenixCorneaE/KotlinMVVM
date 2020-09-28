package com.fphoenixcorneae.openeyes.mvvm.viewmodel.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.GradientDrawable
import com.fphoenixcorneae.framework.glide.GlideUtil
import com.fphoenixcorneae.framework.widget.recyclerview.AbstractRecyclerAdapter
import com.fphoenixcorneae.framework.widget.recyclerview.RecyclerItemType
import com.fphoenixcorneae.framework.widget.recyclerview.ViewHolder
import com.fphoenixcorneae.openeyes.R
import com.fphoenixcorneae.openeyes.mvvm.model.bean.OpenEyesCategoryBean
import com.fphoenixcorneae.util.ColorUtil
import kotlinx.android.synthetic.main.open_eyes_item_category.view.*

/**
 * @desc 分类适配器
 * @date 2020-09-22 16:35
 */
class OpenEyesCategoryAdapter(
    mContext: Context,
    data: ArrayList<OpenEyesCategoryBean>
) :
    AbstractRecyclerAdapter<OpenEyesCategoryBean>(
        mContext,
        data,
        object : RecyclerItemType<OpenEyesCategoryBean> {
            override fun getLayoutId(item: OpenEyesCategoryBean, position: Int): Int {
                return R.layout.open_eyes_item_category
            }
        }) {

    /**
     * 绑定数据
     */
    @SuppressLint("SetTextI18n")
    override fun bindData(holder: ViewHolder, data: OpenEyesCategoryBean, position: Int) {
        with(holder.itemView) {
            data.apply {
                // bgPicture
                GlideUtil.setupImage(
                    mIvCategory,
                    bgPicture,
                    GradientDrawable().apply {
                        setColor(ColorUtil.randomColor)
                    }
                )
                // category name
                mTvCategoryName.text = "#$name"
            }
        }
    }
}
