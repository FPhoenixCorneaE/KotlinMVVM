package com.fphoenixcorneae.openeyes.mvvm.viewmodel.adapter.wrapper

import com.fphoenixcorneae.viewpager.internal.ViewHolder
import com.fphoenixcorneae.viewpager.wrapper.ViewHolderWrapper
import com.fphoenixcorneae.openeyes.R
import com.fphoenixcorneae.openeyes.mvvm.model.bean.OpenEyesHomeBean
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