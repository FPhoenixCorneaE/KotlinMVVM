package com.wkz.kotlinmvvm.mvp.contract

import com.wkz.framework.base.IBaseView
import com.wkz.framework.base.IPresenter
import com.wkz.kotlinmvvm.mvp.model.bean.HomeBean

/**
 * @desc: 关注 契约类
 */
interface FollowContract {

    interface View : IBaseView {
        /**
         * 设置关注信息数据
         */
        fun setFollowInfo(issue: HomeBean.Issue)

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