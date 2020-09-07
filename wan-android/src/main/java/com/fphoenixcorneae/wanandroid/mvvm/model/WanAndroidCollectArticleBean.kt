package com.fphoenixcorneae.wanandroid.mvvm.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 *  @desc: 收藏文章数据
 *  @date: 2020-07-31 09:47
 */
@Parcelize
data class WanAndroidCollectArticleBean(
    var chapterId: Int,
    var author: String,
    var chapterName: String,
    var courseId: Int,
    var desc: String,
    var envelopePic: String,
    var id: Int,
    var link: String,
    var niceDate: String,
    var origin: String,
    var originId: Int,
    var publishTime: Long,
    var title: String,
    var userId: Int,
    var visible: Int,
    var zan: Int
) : Parcelable