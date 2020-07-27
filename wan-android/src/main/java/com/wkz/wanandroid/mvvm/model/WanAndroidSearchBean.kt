package com.wkz.wanandroid.mvvm.model

import android.annotation.SuppressLint
import android.os.Parcelable
import com.fphoenixcorneae.flowlayout.FlowItem
import kotlinx.android.parcel.Parcelize

/**
 * @desc：搜索热词
 * @date：2020-07-18 14:11
 */
@SuppressLint("ParcelCreator")
@Parcelize
data class WanAndroidSearchBean(
    var id: Int = 0,
    var link: String = "",
    var name: String,
    var order: Int = 0,
    var visible: Int = 0
) : Parcelable, FlowItem {
    override fun getItemName(): CharSequence? {
        return name
    }
}
