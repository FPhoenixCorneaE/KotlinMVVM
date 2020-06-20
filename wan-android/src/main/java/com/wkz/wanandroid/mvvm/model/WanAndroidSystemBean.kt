package com.wkz.wanandroid.mvvm.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @desc：广场体系数据
 * @Date: 2020-06-19 10:44
 */
@Parcelize
data class WanAndroidSystemBean(
    var children: ArrayList<WanAndroidClassifyBean>,
    var courseId: Int,
    var id: Int,
    var name: String,
    var order: Int,
    var parentChapterId: Int,
    var userControlSetTop: Boolean,
    var visible: Int
) : Parcelable
