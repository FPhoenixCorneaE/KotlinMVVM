package com.wkz.kotlinmvvm.mvvm.viewmodel.binder

import com.wkz.framework.base.BaseItemViewBinder
import com.wkz.kotlinmvvm.R
import com.wkz.kotlinmvvm.databinding.OpenEyesItemHomeBannerBinding
import com.wkz.kotlinmvvm.mvvm.model.bean.OpenEyesHomeBean

/**
 * @desc: 首页精选横幅轮播图 Binder
 */
class OpenEyesHomeBannerBinder : BaseItemViewBinder<OpenEyesHomeBean.Issue.Item, OpenEyesItemHomeBannerBinding>() {

    override fun getLayoutId(): Int = R.layout.open_eyes_item_home_banner

    override fun setData(holder: ViewHolder, data: OpenEyesHomeBean.Issue.Item) {

    }
}