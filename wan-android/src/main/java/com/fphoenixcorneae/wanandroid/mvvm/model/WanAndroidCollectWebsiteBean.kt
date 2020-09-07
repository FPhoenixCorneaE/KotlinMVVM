package com.fphoenixcorneae.wanandroid.mvvm.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 *  @desc: 收藏网址数据
 *  @date: 2020-07-31 09:48
 */
@Parcelize
data class WanAndroidCollectWebsiteBean(
    var icon: String,
    var id: Int,
    var link: String,
    var name: String,
    var order: Int,
    var userId: Int,
    var visible: Int
) : Parcelable

