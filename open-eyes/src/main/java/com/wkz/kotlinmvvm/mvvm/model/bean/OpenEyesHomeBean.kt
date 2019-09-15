package com.wkz.kotlinmvvm.mvvm.model.bean

import androidx.databinding.BaseObservable
import com.wkz.framework.base.IViewBinder
import java.io.Serializable

/**
 * @desc: 首页（视频详情等）Bean
 */
data class OpenEyesHomeBean(
    val issueList: ArrayList<Issue>,
    val nextPageUrl: String,
    val nextPublishTime: Long,
    val newestIssueType: String,
    val dialog: Any
) : Serializable, BaseObservable() {

    data class Issue(
        val releaseTime: Long,
        val type: String,
        val date: Long,
        val total: Int,
        val publishTime: Long,
        val itemList: ArrayList<Item>,
        var count: Int,
        val nextPageUrl: String
    ) : Serializable, BaseObservable() {

        data class Item(val type: String, val data: Data?, val tag: String) : Serializable, BaseObservable(),
            IViewBinder {

            data class Data(
                val dataType: String,
                val text: String,
                val videoTitle: String,
                val id: Long,
                val title: String,
                val slogan: String?,
                val description: String,
                val actionUrl: String,
                val provider: Provider,
                val category: String,
                val parentReply: ParentReply,
                val author: Author,
                val cover: Cover,
                val likeCount: Int,
                val playUrl: String,
                val thumbPlayUrl: String,
                val duration: Long,
                val message: String,
                val createTime: Long,
                val webUrl: WebUrl,
                val library: String,
                val user: User,
                val playInfo: ArrayList<PlayInfo>?,
                val consumption: Consumption,
                val campaign: Any,
                val waterMarks: Any,
                val adTrack: Any,
                val tags: ArrayList<Tag>,
                val type: String,
                val titlePgc: Any,
                val descriptionPgc: Any,
                val remark: String,
                val idx: Int,
                val shareAdTrack: Any,
                val favoriteAdTrack: Any,
                val webAdTrack: Any,
                val date: Long,
                val promotion: Any,
                val label: Any,
                val labelList: Any,
                val descriptionEditor: String,
                val collected: Boolean,
                val played: Boolean,
                val subtitles: Any,
                val lastViewTime: Any,
                val playlists: Any,
                val header: Header,
                val itemList: ArrayList<OpenEyesHomeBean.Issue.Item>
            ) : Serializable, BaseObservable() {
                data class Tag(val id: Int, val name: String, val actionUrl: String, val adTrack: Any) : Serializable,
                    BaseObservable()

                data class Author(val icon: String, val name: String, val description: String) : Serializable,
                    BaseObservable()

                data class Provider(val name: String, val alias: String, val icon: String) : Serializable,
                    BaseObservable()

                data class Cover(
                    val feed: String, val detail: String,
                    val blurred: String, val sharing: String, val homepage: String
                ) : Serializable, BaseObservable()

                data class WebUrl(val raw: String, val forWeibo: String) : Serializable, BaseObservable()

                data class PlayInfo(val name: String, val url: String, val type: String, val urlList: ArrayList<Url>) :
                    Serializable, BaseObservable()

                data class Consumption(val collectionCount: Int, val shareCount: Int, val replyCount: Int) :
                    Serializable, BaseObservable()

                data class User(
                    val uid: Long,
                    val nickname: String,
                    val avatar: String,
                    val userType: String,
                    val ifPgc: Boolean
                ) : Serializable, BaseObservable()

                data class ParentReply(val user: User, val message: String) : Serializable, BaseObservable()

                data class Url(val size: Long) : Serializable, BaseObservable()

                data class Header(
                    val id: Int,
                    val icon: String,
                    val iconType: String,
                    val description: String,
                    val title: String,
                    val font: String,
                    val cover: String,
                    val label: Label,
                    val actionUrl: String,
                    val subtitle: String,
                    val labelList: ArrayList<Label>
                ) : Serializable, BaseObservable() {
                    data class Label(val text: String, val card: String, val detial: Any, val actionUrl: Any) :
                        Serializable, BaseObservable()
                }
            }
        }
    }
}


//    "issueList": [],
//    "nextPageUrl": "http://baobab.kaiyanapp.com/api/v2/feed?date=1503104400000&num=1",
//    "nextPublishTime": 1503277200000,
//    "newestIssueType": "morning",
//    "dialog": null