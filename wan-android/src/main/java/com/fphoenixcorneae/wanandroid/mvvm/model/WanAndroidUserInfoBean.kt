package com.fphoenixcorneae.wanandroid.mvvm.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

/**
 * @desc：账号信息数据
 * @date：2020-04-23 12:20
 */
@Keep
@Parcelize
data class WanAndroidUserInfoBean(
    var admin: Boolean = false,
    var chapterTops: MutableList<String>? = mutableListOf(),
    var collectIds: MutableList<String>? = mutableListOf(),
    var email: String = "",
    var icon: String = "",
    var id: String = "",
    var nickname: String = "",
    var password: String = "",
    var token: String = "",
    var type: Int = 0,
    var username: String = ""
) : Parcelable
