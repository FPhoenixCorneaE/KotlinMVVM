package com.fphoenixcorneae.wanandroid.mvvm.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

/**
 * @desc：账号登录、注册信息
 * @date：2020-04-23 10:20
 */
@Keep
@Parcelize
data class WanAndroidAccountBody(
    var username: String = "",
    var password: String = ""
) : Parcelable