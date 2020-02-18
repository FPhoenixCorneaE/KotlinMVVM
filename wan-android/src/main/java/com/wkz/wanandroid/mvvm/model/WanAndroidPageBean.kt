package com.wkz.wanandroid.mvvm.model

import java.io.Serializable

data class WanAndroidPageBean(
    var curPage: Int,
    var datas: ArrayList<ArticleBean>,
    var offset: Int,
    var over: Boolean,
    var pageCount: Int,
    var size: Int,
    var total: Int
) : Serializable {
    /**
     * 文章
     */
    data class ArticleBean(
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
    ) : Serializable {
        /**
         * 文章的标签
         */
        data class TagsBean(
            var name: String,
            var url: String
        ) : Serializable
    }
}