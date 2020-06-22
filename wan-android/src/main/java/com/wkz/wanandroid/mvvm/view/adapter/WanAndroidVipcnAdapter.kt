package com.wkz.wanandroid.mvvm.view.adapter

import android.annotation.SuppressLint
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wkz.adapter.BaseNBAdapter
import com.wkz.extension.toHtml
import com.wkz.shinebutton.ShineButton
import com.wkz.wanandroid.R
import com.wkz.wanandroid.mvvm.model.WanAndroidArticleBean
import kotlinx.android.synthetic.main.wan_android_recycler_item_vipcn.view.*

/**
 * @desc: 公众号适配器
 * @date: 2020-06-18 17:42
 */
class WanAndroidVipcnAdapter :
    BaseNBAdapter<WanAndroidArticleBean>() {

    var mOnItemChildClickListener: OnItemChildClickListener? = null
    override fun getLayoutId(): Int = R.layout.wan_android_recycler_item_vipcn

    /**
     * 绑定数据
     */
    @SuppressLint("NewApi")
    override fun onBindData(
        viewHolder: RecyclerView.ViewHolder,
        data: WanAndroidArticleBean,
        position: Int
    ) {
        viewHolder.itemView.apply {
            mTvAuthor.text =
                when {
                    data.author.isNotEmpty() -> {
                        data.author
                    }
                    else -> {
                        data.shareUser
                    }
                }
            mTvTitle.text = data.title.toHtml()
            mSuperChapterName.text = data.superChapterName
            mChapterName.text = data.chapterName
            mTvNiceDate.text = data.niceDate
            mSbCollect.setChecked(data.collect)
            mTvAuthor.setOnClickListener {
                mOnItemChildClickListener?.onClickAuthorName(mTvAuthor, data, position)
            }
            mSbCollect.setOnClickListener {
                mOnItemChildClickListener?.onClickCollectIcon(mSbCollect, data, position)
            }
        }
    }

    interface OnItemChildClickListener {
        /**
         * 作者点击
         */
        fun onClickAuthorName(
            view: TextView,
            data: WanAndroidArticleBean,
            position: Int
        )

        /**
         * 收藏Icon点击
         */
        fun onClickCollectIcon(
            shineButton: ShineButton,
            data: WanAndroidArticleBean,
            position: Int
        )
    }
}