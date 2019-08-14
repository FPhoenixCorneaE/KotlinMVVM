package com.wkz.kotlinmvvm.viewmodel.binder

import com.wkz.framework.base.BaseItemViewBinder
import com.wkz.kotlinmvvm.R
import com.wkz.kotlinmvvm.databinding.OpenEyesItemHomeVideoBinding
import com.wkz.kotlinmvvm.mvp.model.bean.HomeBean

/**
 * @desc: 首页精选日期 Binder
 */
class OpenEyesHomeVideoBinder : BaseItemViewBinder<HomeBean.Issue.Item, OpenEyesItemHomeVideoBinding>() {

    override fun getLayoutId(): Int = R.layout.open_eyes_item_home_video

    override fun setData(holder: ViewHolder, data: HomeBean.Issue.Item) {
        mBinding.data = data
    }
}