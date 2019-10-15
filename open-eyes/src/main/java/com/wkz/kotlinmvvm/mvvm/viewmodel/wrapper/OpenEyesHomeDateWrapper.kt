package com.wkz.kotlinmvvm.mvvm.viewmodel.wrapper

import com.wkz.adapter.internal.ViewHolder
import com.wkz.adapter.wrapper.ViewHolderWrapper
import com.wkz.kotlinmvvm.R
import com.wkz.kotlinmvvm.mvvm.model.bean.OpenEyesHomeBean
import kotlinx.android.synthetic.main.open_eyes_item_home_date.view.*

/**
 * @desc: 首页精选日期 Wrapper
 */
class OpenEyesHomeDateWrapper :
    ViewHolderWrapper<OpenEyesHomeBean.Issue.Item>(R.layout.open_eyes_item_home_date) {

    override fun onBindViewHolder(holder: ViewHolder, item: OpenEyesHomeBean.Issue.Item) {
        holder.itemView.mTvHeader.text = item.data?.text
    }
}