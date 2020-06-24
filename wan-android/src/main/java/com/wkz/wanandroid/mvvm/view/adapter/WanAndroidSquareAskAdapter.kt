package com.wkz.wanandroid.mvvm.view.adapter

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import com.wkz.adapter.BaseNBAdapter
import com.wkz.extension.toHtml
import com.wkz.wanandroid.R
import com.wkz.wanandroid.mvvm.model.WanAndroidArticleBean
import kotlinx.android.synthetic.main.wan_android_recycler_item_square_ask.view.*
import java.util.regex.Pattern

/**
 * @desc: 广场问答适配器
 * @date: 2020-06-22 16:05
 */
class WanAndroidSquareAskAdapter :
    BaseNBAdapter<WanAndroidArticleBean>() {
    override fun getLayoutId(): Int = R.layout.wan_android_recycler_item_square_ask

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
            mTvTitle.text = data.title.run {
                val pattern = Pattern.compile(
                    "${context.getString(R.string.wan_android_title_fragment_home_qa)}\\s*\\|?\\s*"
                )
                val matcher = pattern.matcher(this)
                when {
                    matcher.find() -> {
                        replaceFirst(matcher.group(), "")
                    }
                    else -> {
                        this
                    }
                }
            }.toHtml()
            mTvDesc.text = data.desc.toHtml()
            mTvNiceDate.text = data.niceDate
        }
    }
}