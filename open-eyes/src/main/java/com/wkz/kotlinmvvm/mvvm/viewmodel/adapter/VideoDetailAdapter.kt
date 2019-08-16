package com.wkz.kotlinmvvm.mvvm.viewmodel.adapter

import android.content.Context
import android.graphics.Typeface
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.wkz.extension.dp2px
import com.wkz.extension.durationFormat
import com.wkz.extension.showToast
import com.wkz.framework.glide.GlideApp
import com.wkz.framework.widget.recyclerview.RecyclerItemType
import com.wkz.framework.widget.recyclerview.ViewHolder
import com.wkz.framework.widget.recyclerview.AbstractRecyclerAdapter
import com.wkz.kotlinmvvm.R
import com.wkz.kotlinmvvm.mvvm.model.bean.HomeBean
import com.wkz.util.ContextUtil


/**
 * Created by xuhao on 2017/11/25.
 * desc: 视频详情
 */
class VideoDetailAdapter(mContext: Context, data: ArrayList<HomeBean.Issue.Item>) :
    AbstractRecyclerAdapter<HomeBean.Issue.Item>(mContext, data, object : RecyclerItemType<HomeBean.Issue.Item> {
        override fun getLayoutId(item: HomeBean.Issue.Item, position: Int): Int {
            return when {
                position == 0 ->
                    R.layout.open_eyes_item_video_detail_info

                data[position].type == "textCard" ->
                    R.layout.open_eyes_item_video_text_card

                data[position].type == "videoSmallCard" ->
                    R.layout.open_eyes_item_video_small_card
                else ->
                    throw IllegalAccessException("Api 解析出错了，出现其他类型")
            }
        }
    }) {

    private var textTypeface: Typeface? = null

    init {
        textTypeface = Typeface.createFromAsset(ContextUtil.context.assets, "fonts/FZLanTingHeiS-L-GB-Regular.TTF")
    }

    /**
     * 添加视频的详细信息
     */
    fun addData(item: HomeBean.Issue.Item) {
        mData.clear()
        notifyDataSetChanged()
        mData.add(item)
        notifyItemInserted(0)

    }

    /**
     * 添加相关推荐等数据 Item
     */
    fun addData(item: ArrayList<HomeBean.Issue.Item>) {
        mData.addAll(item)
        notifyItemRangeInserted(1, item.size)

    }

    /**
     * Kotlin的函数可以作为参数，写callback的时候，可以不用interface了
     */

    private var mOnItemClickRelatedVideo: ((item: HomeBean.Issue.Item) -> Unit)? = null


    fun setOnItemDetailClick(mItemRelatedVideo: (item: HomeBean.Issue.Item) -> Unit) {
        this.mOnItemClickRelatedVideo = mItemRelatedVideo
    }


    /**
     * 绑定数据
     */
    override fun bindData(holder: ViewHolder, data: HomeBean.Issue.Item, position: Int) {
        when {
            position == 0 -> setVideoDetailInfo(data, holder)

            data.type == "textCard" -> {
                holder.setText(R.id.tv_text_card, data.data?.text!!)
                //设置方正兰亭细黑简体
                holder.getView<TextView>(R.id.tv_text_card).typeface = textTypeface

            }
            data.type == "videoSmallCard" -> {
                with(holder) {
                    setText(R.id.tv_title, data.data?.title!!)
                    setText(R.id.tv_tag, "#${data.data.category} / ${durationFormat(data.data.duration)}")
                    setImagePath(
                        R.id.iv_video_small_card,
                        object : ViewHolder.HolderImageLoader(data.data.cover.detail) {
                            override fun loadImage(iv: ImageView, path: String) {
                                GlideApp.with(mContext)
                                    .load(path)
                                    .optionalTransform(RoundedCorners(dp2px(4F)))
                                    .into(iv)
                            }
                        })
                }
                // 判断onItemClickRelatedVideo 并使用
                holder.itemView.setOnClickListener { mOnItemClickRelatedVideo?.invoke(data) }

            }
            else -> throw IllegalAccessException("Api 解析出错了，出现其他类型")
        }
    }

    /**
     * 设置视频详情数据
     */
    private fun setVideoDetailInfo(data: HomeBean.Issue.Item, holder: ViewHolder) {
        data.data?.title?.let { holder.setText(R.id.tv_title, it) }
        //视频简介
        data.data?.description?.let { holder.setText(R.id.expandable_text, it) }
        //标签
        holder.setText(R.id.tv_tag, "#${data.data?.category} / ${durationFormat(data.data?.duration)}")
        //喜欢
        holder.setText(R.id.tv_action_favorites, data.data?.consumption?.collectionCount.toString())
        //分享
        holder.setText(R.id.tv_action_share, data.data?.consumption?.shareCount.toString())
        //评论
        holder.setText(R.id.tv_action_reply, data.data?.consumption?.replyCount.toString())

        if (data.data?.author != null) {
            with(holder) {
                setText(R.id.tv_author_name, data.data.author.name)
                setText(R.id.tv_author_desc, data.data.author.description)
                setImagePath(R.id.iv_avatar, object : ViewHolder.HolderImageLoader(data.data.author.icon) {
                    override fun loadImage(iv: ImageView, path: String) {
                        //加载头像
                        GlideApp.with(mContext)
                            .load(path)
                            .circleCrop()
                            .into(iv)
                    }
                })
            }
        } else {
            holder.setViewVisibility(R.id.layout_author_view, View.GONE)
        }

        with(holder) {
            getView<TextView>(R.id.tv_action_favorites).setOnClickListener {
                showToast("喜欢")
            }
            getView<TextView>(R.id.tv_action_share).setOnClickListener {
                showToast("分享")
            }
            getView<TextView>(R.id.tv_action_reply).setOnClickListener {
                showToast("评论")
            }
        }
    }


}
