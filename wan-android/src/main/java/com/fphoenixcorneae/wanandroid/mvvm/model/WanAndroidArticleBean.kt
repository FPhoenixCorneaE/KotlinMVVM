package com.fphoenixcorneae.wanandroid.mvvm.model

import android.os.Parcelable
import com.fphoenixcorneae.flowlayout.FlowItem
import com.fphoenixcorneae.ext.toHtml
import kotlinx.android.parcel.Parcelize

/**
 * @desc：文章数据
 * @date：2019-10-24 13:24
 */
@Parcelize
data class WanAndroidArticleBean(
    var apkLink: String,
    var author: String,
    var chapterId: Int,
    var chapterName: String,
    var collect: Boolean,
    var courseId: Int,
    var desc: String,
    var envelopePic: String,
    var fresh: Boolean,
    var id: Int,
    var link: String,
    var niceDate: String,
    var origin: String,
    var prefix: String,
    var projectLink: String,
    var publishTime: Long,
    var superChapterId: Int,
    var superChapterName: String,
    var shareUser: String,
    var tags: List<TagsBean>,
    var title: String,
    var type: Int,
    var userId: Int,
    var visible: Int,
    var zan: Int
) : Parcelable, FlowItem {
    override fun getItemName(): CharSequence? {
        return title.toHtml()
    }

    /**
     * 文章的标签
     */
    @Parcelize
    data class TagsBean(
        var name: String,
        var url: String
    ) : Parcelable
}