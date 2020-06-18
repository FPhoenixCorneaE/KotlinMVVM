package com.wkz.wanandroid.mvvm.model

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @desc：项目分类
 * @date：2020-06-16 10:32
 */
@SuppressLint("ParcelCreator")
@Parcelize
data class WanAndroidProjectClassifyBean(
    var children: List<String> = listOf(),
    var courseId: Int = 0,
    var id: Int = 0,
    var name: String = "",
    var order: Int = 0,
    var parentChapterId: Int = 0,
    var userControlSetTop: Boolean = false,
    var visible: Int = 0
) : Parcelable