package com.fphoenixcorneae.wanandroid.mvvm.view.adapter

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import com.fphoenixcorneae.viewpager.BaseNBAdapter
import com.fphoenixcorneae.ext.dp2px
import com.fphoenixcorneae.util.ResourceUtil
import com.fphoenixcorneae.wanandroid.R
import com.fphoenixcorneae.wanandroid.manager.WanAndroidUserManager
import com.fphoenixcorneae.wanandroid.mvvm.model.WanAndroidIntegralBean
import kotlinx.android.synthetic.main.wan_android_fragment_integral_ranking.view.mTvCoinCount
import kotlinx.android.synthetic.main.wan_android_fragment_integral_ranking.view.mTvUserName
import kotlinx.android.synthetic.main.wan_android_fragment_integral_ranking.view.mTvUserRanking
import kotlinx.android.synthetic.main.wan_android_recycler_item_mine_integral_ranking.view.*

/**
 * @desc: 积分排行榜适配器
 * @date: 2020-06-08 17:21
 */
class WanAndroidIntegralRankingAdapter :
    BaseNBAdapter<WanAndroidIntegralBean>() {

    private var mUserId: Int = 0

    override fun getLayoutId(): Int = R.layout.wan_android_recycler_item_mine_integral_ranking

    /**
     * 绑定数据
     */
    @SuppressLint("NewApi")
    override fun onBindData(
        viewHolder: RecyclerView.ViewHolder,
        data: WanAndroidIntegralBean,
        position: Int
    ) {
        viewHolder.itemView.apply {
            val itemLayoutParams = mCvItem.layoutParams as RecyclerView.LayoutParams
            itemLayoutParams.topMargin = when (viewHolder.adapterPosition) {
                0 -> context.dp2px(16f)
                else -> context.dp2px(8f)
            }
            when (data.userId) {
                mUserId -> {
                    mTvUserRanking.setTextColor(ResourceUtil.getColor(R.color.wan_android_colorAccent))
                    mTvUserName.setTextColor(ResourceUtil.getColor(R.color.wan_android_colorAccent))
                    mTvCoinCount.setTextColor(ResourceUtil.getColor(R.color.wan_android_colorAccent))
                }
                else -> {
                    mTvUserRanking.setTextColor(ResourceUtil.getColor(R.color.wan_android_color_title_0x222222))
                    mTvUserName.setTextColor(ResourceUtil.getColor(R.color.wan_android_color_gray_0x666666))
                    mTvCoinCount.setTextColor(ResourceUtil.getColor(R.color.wan_android_color_gray_0x666666))
                }
            }
            mTvUserRanking.text = (viewHolder.adapterPosition + 1).toString()
            mTvUserName.text = data.username
            mTvCoinCount.text = data.coinCount.toString()
        }
    }

    init {
        mUserId = WanAndroidUserManager.sUserInfo?.id?.toInt() ?: 0
    }
}