package com.fphoenixcorneae.kotlinmvvm.mvvm.viewmodel.wrapper

import com.fphoenixcorneae.adapter.internal.ViewHolder
import com.fphoenixcorneae.adapter.wrapper.ViewHolderWrapper
import com.fphoenixcorneae.kotlinmvvm.R
import com.fphoenixcorneae.kotlinmvvm.mvvm.model.bean.OpenEyesHomeBean

/**
 * @desc: 首页精选横幅轮播图 Wrapper
 */
class OpenEyesHomeBannerWrapper :
    ViewHolderWrapper<OpenEyesHomeBean.Issue.Item>(R.layout.open_eyes_item_home_banner) {

    override fun onBindViewHolder(holder: ViewHolder, item: OpenEyesHomeBean.Issue.Item) {
    }
}