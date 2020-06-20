package com.wkz.wanandroid.mvvm.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @desc：广场导航数据
 * @Date：2020-06-19 10:46
 */
@Parcelize
data class WanAndroidNavigationBean(
    var articles: ArrayList<WanAndroidArticleBean>,
    var cid: Int,
    var name: String
) : Parcelable
