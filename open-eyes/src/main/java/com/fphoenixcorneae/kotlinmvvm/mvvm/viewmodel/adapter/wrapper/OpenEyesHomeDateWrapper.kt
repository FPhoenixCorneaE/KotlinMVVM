package com.fphoenixcorneae.kotlinmvvm.mvvm.viewmodel.adapter.wrapper

import com.fphoenixcorneae.adapter.internal.ViewHolder
import com.fphoenixcorneae.adapter.wrapper.ViewHolderWrapper
import com.fphoenixcorneae.kotlinmvvm.R
import com.fphoenixcorneae.kotlinmvvm.mvvm.model.bean.OpenEyesHomeBean
import kotlinx.android.synthetic.main.open_eyes_item_home_date.view.*

/**
 * @desc: 首页精选日期 Wrapper
 */
class OpenEyesHomeDateWrapper :
    ViewHolderWrapper<OpenEyesHomeBean.Issue.Item>(R.layout.open_eyes_item_home_date) {

    override fun onBindViewHolder(holder: ViewHolder, item: OpenEyesHomeBean.Issue.Item) {
        with(holder.itemView) {
            mTvHeader.text = item.data?.text
        }
    }
}