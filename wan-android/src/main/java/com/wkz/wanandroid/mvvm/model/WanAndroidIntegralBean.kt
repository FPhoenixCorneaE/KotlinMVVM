package com.wkz.wanandroid.mvvm.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 *  @desc: 积分数据
 *  @date: 2020-05-17 20:36
 */
@Parcelize
data class WanAndroidIntegralBean(
    var coinCount: Int = 0,//当前积分
    var level: Int = 0,//当前级别
    var rank: Int = 0,//当前排名
    var userId: Int = 0,
    var username: String = ""
) : Parcelable


