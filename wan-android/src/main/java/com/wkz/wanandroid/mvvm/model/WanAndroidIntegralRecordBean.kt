package com.wkz.wanandroid.mvvm.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 *  @desc: 积分记录数据
 *  @date: 2020-06-10 13:17
 */
@Parcelize
data class WanAndroidIntegralRecordBean(
    var coinCount: Int = 0,
    var date: Long = 0,
    var desc: String = "",
    var id: Int = 0,
    var type: Int = 0,
    var reason: String = "",
    var userId: Int = 0,
    var userName: String = ""
) : Parcelable


