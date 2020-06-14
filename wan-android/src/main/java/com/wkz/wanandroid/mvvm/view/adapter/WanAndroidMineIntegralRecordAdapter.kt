package com.wkz.wanandroid.mvvm.view.adapter

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import com.wkz.adapter.BaseNBAdapter
import com.wkz.extension.dp2px
import com.wkz.util.TimeUtil
import com.wkz.wanandroid.R
import com.wkz.wanandroid.mvvm.model.WanAndroidIntegralRecordBean
import kotlinx.android.synthetic.main.wan_android_recycler_item_mine_integral_record.view.*

/**
 * @desc: 积分记录适配器
 * @date: 2020-06-10 17:20
 */
class WanAndroidMineIntegralRecordAdapter :
    BaseNBAdapter<WanAndroidIntegralRecordBean>() {

    override fun getLayoutId(): Int = R.layout.wan_android_recycler_item_mine_integral_record

    /**
     * 绑定数据
     */
    @SuppressLint("NewApi")
    override fun onBindData(
        viewHolder: RecyclerView.ViewHolder,
        data: WanAndroidIntegralRecordBean,
        position: Int
    ) {
        viewHolder.itemView.apply {
            val itemLayoutParams = mCvItem.layoutParams as RecyclerView.LayoutParams
            itemLayoutParams.topMargin = when (viewHolder.adapterPosition) {
                0 -> context.dp2px(16f)
                else -> context.dp2px(8f)
            }
            mTvIntegralDesc.text = data.reason.plus(
                when {
                    data.desc.contains(context.getString(R.string.wan_android_mine_integral)) -> {
                        data.desc.substring(data.desc.indexOf(context.getString(R.string.wan_android_mine_integral)))
                    }
                    else -> ""
                }
            )
            mTvIntegralDate.text = TimeUtil.getFriendlyTimeSpanByNow(data.date)
            mTvCoinCount.text = data.coinCount.toString()
        }
    }
}