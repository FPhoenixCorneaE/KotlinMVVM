package com.wkz.wanandroid.mvvm.model

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @desc：搜索热词
 * @date：2020-07-18 14:11
 */
@SuppressLint("ParcelCreator")
@Parcelize
data class WanAndroidSearchBean(
    var id: Int,
    var link: String,
    var name: String,
    var order: Int,
    var visible: Int
) : Parcelable
