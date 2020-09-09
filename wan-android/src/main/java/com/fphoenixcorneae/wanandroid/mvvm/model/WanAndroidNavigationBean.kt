package com.fphoenixcorneae.wanandroid.mvvm.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

/**
 * @desc：广场导航数据
 * @Date：2020-06-19 10:46
 */
@Keep
@Parcelize
data class WanAndroidNavigationBean(
    var articles: ArrayList<WanAndroidArticleBean>,
    var cid: Int,
    var name: String
) : Parcelable
