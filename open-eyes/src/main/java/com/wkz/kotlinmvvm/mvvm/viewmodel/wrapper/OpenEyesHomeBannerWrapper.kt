package com.wkz.kotlinmvvm.mvvm.viewmodel.wrapper

import com.wkz.adapter.internal.ViewHolder
import com.wkz.adapter.wrapper.ViewHolderWrapper
import com.wkz.kotlinmvvm.R
import com.wkz.kotlinmvvm.mvvm.model.bean.OpenEyesHomeBean

/**
 * @desc: 首页精选横幅轮播图 Wrapper
 */
class OpenEyesHomeBannerWrapper :
    ViewHolderWrapper<OpenEyesHomeBean.Issue.Item>(R.layout.open_eyes_item_home_banner) {

    override fun onBindViewHolder(holder: ViewHolder, item: OpenEyesHomeBean.Issue.Item) {
    }
}