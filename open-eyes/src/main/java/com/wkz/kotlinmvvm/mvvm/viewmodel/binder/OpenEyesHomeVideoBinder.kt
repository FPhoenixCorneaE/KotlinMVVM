package com.wkz.kotlinmvvm.mvvm.viewmodel.binder

import com.wkz.framework.base.BaseItemViewBinder
import com.wkz.kotlinmvvm.BR
import com.wkz.kotlinmvvm.R
import com.wkz.kotlinmvvm.databinding.OpenEyesItemHomeVideoBinding
import com.wkz.kotlinmvvm.mvvm.model.bean.HomeBean

/**
 * @desc: 首页精选日期 Binder
 */
class OpenEyesHomeVideoBinder : BaseItemViewBinder<HomeBean.Issue.Item, OpenEyesItemHomeVideoBinding>() {

    override fun getLayoutId(): Int = R.layout.open_eyes_item_home_video

    override fun setData(holder: ViewHolder, data: HomeBean.Issue.Item) {
        mBinding.setVariable(BR.data, data)
        // 迫使数据立即绑定而不是在下一帧的时候才绑定
        // 假设没使用executePendingBindings()方法，由于在下一帧的时候才会绑定，
        // view就会绑定错误的data，测量也会出错。
        // 因此，executePendingBindings()是很重要的。
        mBinding.executePendingBindings()
    }
}