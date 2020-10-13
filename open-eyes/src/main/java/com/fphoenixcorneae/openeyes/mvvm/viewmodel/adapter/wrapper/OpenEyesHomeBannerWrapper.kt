package com.fphoenixcorneae.openeyes.mvvm.viewmodel.adapter.wrapper

import com.fphoenixcorneae.viewpager.internal.ViewHolder
import com.fphoenixcorneae.viewpager.wrapper.ViewHolderWrapper
import com.fphoenixcorneae.openeyes.R
import com.fphoenixcorneae.openeyes.mvvm.model.bean.OpenEyesHomeBean

/**
 * @desc: 首页精选横幅轮播图 Wrapper
 */
class OpenEyesHomeBannerWrapper :
    ViewHolderWrapper<OpenEyesHomeBean.Issue.Item>(R.layout.open_eyes_item_home_banner) {

    override fun onBindViewHolder(holder: ViewHolder, item: OpenEyesHomeBean.Issue.Item) {
    }
}