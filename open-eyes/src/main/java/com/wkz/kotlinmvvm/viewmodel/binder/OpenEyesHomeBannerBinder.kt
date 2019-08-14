package com.wkz.kotlinmvvm.viewmodel.binder

import com.wkz.framework.base.BaseItemViewBinder
import com.wkz.kotlinmvvm.R
import com.wkz.kotlinmvvm.databinding.OpenEyesItemHomeBannerBinding
import com.wkz.kotlinmvvm.mvp.model.bean.HomeBean

/**
 * @desc: 首页精选横幅轮播图 Binder
 */
class OpenEyesHomeBannerBinder : BaseItemViewBinder<HomeBean.Issue.Item, OpenEyesItemHomeBannerBinding>() {

    override fun getLayoutId(): Int = R.layout.open_eyes_item_home_banner

    override fun setData(holder: ViewHolder, data: HomeBean.Issue.Item) {

    }
}