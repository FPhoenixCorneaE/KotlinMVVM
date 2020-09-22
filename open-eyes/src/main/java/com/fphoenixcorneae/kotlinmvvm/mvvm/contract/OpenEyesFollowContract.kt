package com.fphoenixcorneae.kotlinmvvm.mvvm.contract

import com.fphoenixcorneae.framework.base.IPresenter
import com.fphoenixcorneae.framework.base.IView
import com.fphoenixcorneae.kotlinmvvm.mvvm.model.bean.OpenEyesHomeBean

/**
 * @desc: 关注 契约类
 */
interface OpenEyesFollowContract {

    interface View : IView {
        /**
         * 设置关注信息数据
         */
        fun setFollowInfo(issue: OpenEyesHomeBean.Issue)

        /**
         * 加载更多关注信息数据
         */
        fun loadMoreFollowInfo(issue: OpenEyesHomeBean.Issue)

    }


    interface Presenter : IPresenter<View> {
        /**
         * 获取关注List
         */
        fun requestFollowList()

        /**
         * 加载更多
         */
        fun loadMoreData()
    }
}