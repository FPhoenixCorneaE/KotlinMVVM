package com.wkz.kotlinmvvm.mvvm.contract

import com.wkz.framework.base.IView
import com.wkz.framework.base.IPresenter
import com.wkz.kotlinmvvm.mvvm.model.bean.OpenEyesHomeBean

/**
 * @desc: 关注 契约类
 */
interface OpenEyesFollowContract {

    interface View : IView {
        /**
         * 设置关注信息数据
         */
        fun setFollowInfo(issue: OpenEyesHomeBean.Issue)

        fun showError(errorMsg: String, errorCode: Int)
    }


    interface Presenter : IPresenter<View> {
        /**
         * 获取List
         */
        fun requestFollowList()

        /**
         * 加载更多
         */
        fun loadMoreData()
    }
}