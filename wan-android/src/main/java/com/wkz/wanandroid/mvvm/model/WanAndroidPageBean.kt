package com.wkz.wanandroid.mvvm.model

data class WanAndroidPageBean(
    var curPage: Int,
    var datas: List<ArticleBean>,
    var offset: Int,
    var over: Boolean,
    var pageCount: Int,
    var size: Int,
    var total: Int
) {
    data class ArticleBean(
        var id: Int,
        var author: String,
        var chapterId: Int,
        var chapterName: String,
        var collect: Boolean,
        var fresh: Boolean,
        var courseId: Int,
        var desc: String,
        var link: String,
        var niceDate: String,
        var publishTime: Long,
        var superChapterId: Int,
        var superChapterName: String,
        var title: String,
        var envelopePic: String,
        var read: Boolean,
        var originId: Int
    )
}