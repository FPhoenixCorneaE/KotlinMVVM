package com.wkz.wanandroid.mvvm.view.adapter

import androidx.recyclerview.widget.RecyclerView
import com.wkz.adapter.BaseNBAdapter
import com.wkz.framework.glide.GlideUtil
import com.wkz.util.ScreenUtil
import com.wkz.util.SizeUtil
import com.wkz.wanandroid.R
import com.wkz.wanandroid.mvvm.model.WanAndroidBannerBean
import kotlinx.android.synthetic.main.wan_android_recycler_item_home_banner.view.*

/**
 * @desc: 首页Banner适配器
 * @date: 2019-11-05 14:36
 */
class WanAndroidHomeBannerAdapter : BaseNBAdapter<WanAndroidBannerBean>() {

    override fun getLayoutId(): Int = R.layout.wan_android_recycler_item_home_banner

    /**
     * 绑定数据
     */
    override fun onBindData(
        viewHolder: RecyclerView.ViewHolder,
        data: WanAndroidBannerBean,
        position: Int
    ) {
        val layoutParams = viewHolder.itemView.mCvItem.layoutParams as RecyclerView.LayoutParams
        layoutParams.width = ScreenUtil.screenWidth - SizeUtil.dp2px(32F)
        GlideUtil.setupImage(viewHolder.itemView.mIvImg, data.imagePath)
    }
}